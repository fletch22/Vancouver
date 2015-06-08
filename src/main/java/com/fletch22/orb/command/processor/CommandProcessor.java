package com.fletch22.orb.command.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.dao.LogBundler;
import com.fletch22.dao.LogBundler.LogBundleDto;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.cache.local.OrbManagerLocalCache;
import com.fletch22.orb.cache.local.OrbTypeManagerLocalCache;
import com.fletch22.orb.command.ActionSniffer;
import com.fletch22.orb.command.CommandBundle;
import com.fletch22.orb.command.orb.AddOrbCommand;
import com.fletch22.orb.command.orbType.AddBaseOrbTypeCommand;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.command.transaction.BeginTransactionCommand;
import com.fletch22.orb.command.transaction.CommitTransactionCommand;
import com.fletch22.orb.command.transaction.CommitTransactionDto;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.rollback.UndoAction;
import com.fletch22.orb.rollback.UndoActionBundle;
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
	UndoService rollbackService;

	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;

	public OperationResult processAction(CommandProcessActionPackage commandProcessActionPackage) {

		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		operationResult = executeAction(commandProcessActionPackage);
		operationResult.internalIdAfterOperation = this.internalIdGenerator.getCurrentId();

		tryHandlingAndRollback(commandProcessActionPackage, operationResult);

		return operationResult;
	}

	private void tryHandlingAndRollback(CommandProcessActionPackage commandProcessActionPackage, OperationResult operationResult) {
		try {
			handleLoggingAndRollback(commandProcessActionPackage, operationResult);
		} catch (Exception e) {
			Exception explained = new Exception("Orb DB and SQL DB are now out of sync. Restart is recommended.", e);
			throw new RuntimeException(explained);
		}
	}

	private void handleLoggingAndRollback(CommandProcessActionPackage commandProcessActionPackage, OperationResult operationResult) {
		if (operationResult.shouldBeLogged && !commandProcessActionPackage.isInRestoreMode() && operationResult.opResult == OpResult.SUCCESS) {
			logActionService.logAction(operationResult, commandProcessActionPackage);
		}

		if (operationResult.opResult != OpResult.SUCCESS) {
			this.rollbackService.undoActions(commandProcessActionPackage.getUndoActionBundle());
		}
	}

	public OperationResult executeAction(CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;

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

	private OperationResult execute(AddOrbDto addOrbDto, CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;

		try {
			Orb orb = this.orbManager.createOrbInstance(addOrbDto, commandProcessActionPackage.getTranDate(), commandProcessActionPackage.getUndoActionBundle());
			operationResult = new OperationResult(OpResult.SUCCESS, orb, true);
		} catch (Exception e) {
			operationResult.opResult = OpResult.FAILURE;
			operationResult.operationResultException = e;
		}

		return operationResult;
	}

	private OperationResult execute(CommitTransactionDto commitTransactionDto, CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;

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
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;

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
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;

		for (StringBuilder action : commandBundle.getActionList()) {
			commandProcessActionPackage.setAction(action);
			operationResult = executeAction(commandProcessActionPackage);

			if (operationResult.opResult == OpResult.FAILURE) break;
		}

		return operationResult;
	}

	private OperationResult execute(UndoActionBundle undoActionBundle, final CommandProcessActionPackage commandProcessActionPackage) {
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;

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

		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
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

		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
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
