package com.fletch22.orb.command.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.command.ActionSniffer;
import com.fletch22.command.CommandFactory;
import com.fletch22.dao.LogActionService;
import com.fletch22.dao.LogBundler;
import com.fletch22.dao.LogBundler.LogBundleDto;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.command.processor.OperationResult.OpResult;

@Component
public class CommandProcessor {
	
	Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
	
	@Autowired
	CommandFactory commandFactory;
	
	@Autowired
	ActionSniffer actionSniffer;
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	LogActionService logActionService;
	
	@Autowired
	LogBundler logBundler;

	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	public OperationResult processAction(CommandProcessActionPackage commandProcessActionPackage) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		operationResult = executeAction(commandProcessActionPackage);
		
		operationResult.internalIdAfterOperation = this.internalIdGenerator.getCurrentId();
		
		handleLoggingAndRollback(commandProcessActionPackage);
		
		return operationResult;
	}

	private void handleLoggingAndRollback(CommandProcessActionPackage commandProcessActionPackage) {
		if (commandProcessActionPackage.operationResult.shouldBeLogged
		&& !commandProcessActionPackage.isInRestoreMode) {
			logActionService.logAction(commandProcessActionPackage.operationResult);
		}
	}

	private OperationResult executeAction(CommandProcessActionPackage commandProcessActionPackage) {
		StringBuilder action = commandProcessActionPackage.action;
		
		String actionVerb = this.actionSniffer.getVerb(action);
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		switch (actionVerb) {
			case CommandExpressor.ADD_ORB_TYPE:
				AddOrbTypeDto addOrbTypeDto = this.addOrbTypeCommand.fromJson(action.toString());
				operationResult = execute(addOrbTypeDto, commandProcessActionPackage);
				break;
			case CommandExpressor.LOG_BUNDLE:
				LogBundleDto logBundleDto = this.logBundler.unbundle(action);
				operationResult = execute(logBundleDto, commandProcessActionPackage);
			default:
				throw new RuntimeException("Encountered problem trying to determine json command type from '" + action + "'.");
		}
		
		operationResult.action = new StringBuilder(action);
		
		return operationResult;
	}
	
	private OperationResult execute(LogBundleDto logBundle, CommandProcessActionPackage commandProcessActionPackage) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();
	
		try {
			this.internalIdGenerator.setCurrentId(logBundle.internalIdBeforeOperation);
			commandProcessActionPackage.action = logBundle.action;
			
			operationResult = processAction(commandProcessActionPackage);
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}
		
		return operationResult;
	}

	public OperationResult execute(AddOrbTypeDto addOrbTypeDto, CommandProcessActionPackage commandProcessActionPackage) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		
		try {
			operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();
			long orbInternalId = this.orbTypeManager.createOrbType(addOrbTypeDto, commandProcessActionPackage.tranDate, commandProcessActionPackage.operationResult.rollbackAction);
			
			operationResult = new OperationResult(OpResult.SUCCESS, orbInternalId, true);
			operationResult.rollbackAction = commandProcessActionPackage.operationResult.rollbackAction;
			operationResult.tranDate = commandProcessActionPackage.tranDate;
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}
		
		return operationResult;
	}
}
