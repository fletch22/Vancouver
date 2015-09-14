package com.fletch22.orb.command.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.dao.LogActionService;
import com.fletch22.dao.LogBundler;
import com.fletch22.dao.LogBundler.LogBundleDto;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.ActionSniffer;
import com.fletch22.orb.command.CommandBundle;
import com.fletch22.orb.command.MethodCallCommand;
import com.fletch22.orb.command.orb.AddOrbCommand;
import com.fletch22.orb.command.orbType.AddBaseOrbTypeCommand;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.GetOrbTypeCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.GetOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.MethodCallDto;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.command.transaction.BeginTransactionCommand;
import com.fletch22.orb.command.transaction.CommitTransactionCommand;
import com.fletch22.orb.command.transaction.CommitTransactionDto;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder;
import com.fletch22.orb.rollback.UndoAction;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.orb.service.MethodCallService;
import com.fletch22.orb.transaction.UndoService;

@Component
public class CommandProcessor {

	Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

	@Autowired
	ActionSniffer actionSniffer;

	@Autowired
	AddBaseOrbTypeCommand addOrbBaseTypeCommand;
	
	@Autowired
	AddOrbCommand addOrbCommand;

	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;

	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand;

	@Autowired
	BeginTransactionCommand beginTransactionCommand;

	@Autowired
	CommitTransactionCommand commitTransactionCommand;

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	OrbManager orbManager;
	
	@Autowired
	GetOrbTypeCommand getOrbTypeCommand;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;

	@Autowired
	TranDateGenerator tranDateGenerator;

	@Autowired
	LogActionService logActionService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	LogBundler logBundler;

	@Autowired
	UndoService undoService;
	
	@Autowired
	MethodCallCommand methodCallCommand;
	
	@Autowired
	MethodCallService methodCallService;
	
	@Autowired
	EventLogCommandProcessPackageHolder eventLogCommandProcessPackageHolder;

	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	@Autowired
	RedoAndUndoLogging redoAndUndoLogging;

	public OperationResult processAction(CommandProcessActionPackage commandProcessActionPackage) {

		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();
		try {
			Log4EventAspect.isInvokeFromSerializedMethod = true;
			operationResult = executeAction(commandProcessActionPackage);
			operationResult.internalIdAfterOperation = this.internalIdGenerator.getCurrentId();
			redoAndUndoLogging.logRedoAndUndo(commandProcessActionPackage, operationResult);
		} finally {
			Log4EventAspect.isInvokeFromSerializedMethod = false;
		}

		return operationResult;
	}

	public OperationResult executeAction(CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();

		try {
			StringBuilder action = commandProcessActionPackage.getAction();
			
			String actionVerb = this.actionSniffer.getVerb(action);
			switch (actionVerb) {
			case CommandExpressor.ADD_ORB_TYPE:
				AddOrbTypeDto addOrbTypeDto = this.addOrbTypeCommand.fromJson(action.toString());
				operationResult = execute(addOrbTypeDto, commandProcessActionPackage);
				break;
			case CommandExpressor.ADD_ORB:
				AddOrbDto addOrbDto = this.addOrbCommand.fromJson(action.toString());
				operationResult = execute(addOrbDto, commandProcessActionPackage);
				break;
			case CommandExpressor.BEGIN_TRANSACTION:
				this.beginTransactionCommand.fromJson(action.toString());
				operationResult = executeBeginTransaction(commandProcessActionPackage);
				break;
			case CommandExpressor.GET_ORB_TYPE:
				GetOrbTypeDto getOrbTypeDto = this.getOrbTypeCommand.fromJson(action.toString());
				operationResult = execute(getOrbTypeDto, commandProcessActionPackage);
				break;
			case CommandExpressor.COMMIT_TRANSACTION_WITH_ID:
				CommitTransactionDto commitTransactionDto = this.commitTransactionCommand.fromJson(action.toString());
				operationResult = execute(commitTransactionDto, commandProcessActionPackage);
				break;
			case CommandExpressor.LOG_BUNDLE:
				LogBundleDto logBundleDto = this.logBundler.unbundle(action);
				operationResult = execute(logBundleDto, commandProcessActionPackage);
				break;
			case CommandExpressor.REMOVE_ORB_TYPE:
				DeleteOrbTypeDto deleteOrbTypeDto = this.deleteOrbTypeCommand.fromJson(action.toString());
				operationResult = execute(deleteOrbTypeDto, commandProcessActionPackage);
				break;
			case CommandExpressor.COMMAND_BUNDLE:
				CommandBundle commandBundle = CommandBundle.fromJson(action);
				operationResult = execute(commandBundle, commandProcessActionPackage);
				break;
			case CommandExpressor.UNDO_BUNDLE:
				UndoActionBundle undoActionBundle = UndoActionBundle.fromJson(action);
				operationResult = execute(undoActionBundle, commandProcessActionPackage);
				break;
			case CommandExpressor.METHOD_CALL:
				MethodCallDto methodCallDto = this.methodCallCommand.fromJson(action);
				operationResult = execute(methodCallDto, commandProcessActionPackage);
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine json command type from '" + action + "'.");
			}
			operationResult.action = new StringBuilder(action);
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult execute(GetOrbTypeDto getOrbTypeDto, CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();
		
		eventLogCommandProcessPackageHolder.setCommandProcessActionPackage(commandProcessActionPackage);
		
		try {
			OrbType orbType = this.orbTypeManager.getOrbType(getOrbTypeDto.orbTypeInternalId);
			operationResult = new OperationResult(OpResult.SUCCESS, orbType, true);
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult execute(MethodCallDto methodCallDto, CommandProcessActionPackage commandProcessActionPackage) {
		
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();
		
		eventLogCommandProcessPackageHolder.setCommandProcessActionPackage(commandProcessActionPackage);
		
		try {
			Object object = methodCallService.process(methodCallDto);
			operationResult = new OperationResult(OpResult.SUCCESS, object, true);
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult execute(AddOrbDto addOrbDto, CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();

		try {
			Orb orb = this.orbManager.createOrb(addOrbDto, commandProcessActionPackage.getTranDate(), commandProcessActionPackage.getUndoActionBundle());
			operationResult = new OperationResult(OpResult.SUCCESS, orb, true);
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult execute(CommitTransactionDto commitTransactionDto, CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();

		try {
			transactionService.commitTransaction();
			operationResult.opResult = OpResult.SUCCESS;
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult executeBeginTransaction(CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();

		try {
			operationResult.operationResultObject = transactionService.beginTransaction(commandProcessActionPackage.getTranId());
			operationResult.opResult = OpResult.SUCCESS;
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult execute(CommandBundle commandBundle, final CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();

		for (StringBuilder action : commandBundle.getActionList()) {
			commandProcessActionPackage.setAction(action);
			operationResult = executeAction(commandProcessActionPackage);

			if (operationResult.opResult == OpResult.FAILURE) break;
		}

		return operationResult;
	}

	private OperationResult execute(UndoActionBundle undoActionBundle, final CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();

		while (!undoActionBundle.getActions().empty()) {
			UndoAction undoAction = undoActionBundle.getActions().pop();
			commandProcessActionPackage.setAction(undoAction.action).setTranDate(undoAction.tranDate);
			operationResult = executeAction(commandProcessActionPackage);

			if (operationResult.opResult == OpResult.FAILURE)
				break;
		}

		return operationResult;
	}

	private OperationResult execute(LogBundleDto logBundle, CommandProcessActionPackage commandProcessActionPackage) {

		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();
		operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();

		try {
			this.internalIdGenerator.setCurrentId(logBundle.internalIdBeforeOperation);
			commandProcessActionPackage.setAction(logBundle.action);

			operationResult = processAction(commandProcessActionPackage);
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}

		return operationResult;
	}

	public OperationResult execute(AddOrbTypeDto addOrbTypeDto, CommandProcessActionPackage commandProcessActionPackage) {

		OperationResult operationResult = new OperationResult(OpResult.IN_THE_MIDDLE);

		try {
			operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();
			long orbInternalId = this.orbTypeManager.createOrbType(addOrbTypeDto, commandProcessActionPackage.getTranDate(), commandProcessActionPackage.getUndoActionBundle());

			operationResult = new OperationResult(OpResult.SUCCESS, orbInternalId, true);
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}

		return operationResult;
	}

	private OperationResult execute(DeleteOrbTypeDto deleteOrbTypeDto, CommandProcessActionPackage commandProcessActionPackage) {

		OperationResult operationResult = OperationResult.getInstanceInTheMiddle();
		operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();

		try {
			this.orbTypeManager.deleteOrbType(deleteOrbTypeDto, commandProcessActionPackage.getTranDate(), commandProcessActionPackage.getUndoActionBundle());

			operationResult = new OperationResult(OpResult.SUCCESS, true);
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}

		return operationResult;
	}
}
