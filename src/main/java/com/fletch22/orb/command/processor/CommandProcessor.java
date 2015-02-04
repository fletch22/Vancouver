package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.command.ActionSniffer;
import com.fletch22.command.CommandFactory;
import com.fletch22.dao.LogActionService;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.rollback.RollbackAction;

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
	
	public OperationResult processAction(StringBuilder action) {
		return processAction(action, tranDateGenerator.getTranDate());
	}
	
	public OperationResult processAction(StringBuilder action, BigDecimal tranDate) {
		return processAction(action, tranDate, new RollbackAction());
	}
 
	public OperationResult processAction(StringBuilder action, BigDecimal tranDate, RollbackAction rollbackAction) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		operationResult = executeAction(action, tranDate, rollbackAction);
		
		operationResult.internalIdAfterOperation = this.internalIdGenerator.getCurrentId();
		operationResult.rollbackAction = rollbackAction;
		
		handleLoggingAndRollback(operationResult);
		
		return operationResult;
	}

	private void handleLoggingAndRollback(OperationResult operationResult) {
		
		if (operationResult.shouldBeLogged) {
			logActionService.logAction(operationResult);
		}
	}

	private OperationResult executeAction(StringBuilder action, BigDecimal tranDate, RollbackAction rollbackAction) {
		String actionVerb = actionSniffer.getVerb(action);
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		switch (actionVerb) {
			case CommandExpressor.ADD_ORB_TYPE:
				AddOrbTypeDto addOrbTypeDto = this.addOrbTypeCommand.fromJson(action.toString());
				operationResult = execute(addOrbTypeDto, tranDate, rollbackAction);
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine json command type from '" + action + "'.");
		}
		
		operationResult.action = new StringBuilder(action);
		
		return operationResult;
	}
	
	public OperationResult execute(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, RollbackAction rollbackAction) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();
		
		try {
			long orbInternalId = orbTypeManager.createOrbType(addOrbTypeDto, tranDate, rollbackAction);
			
			operationResult = new OperationResult(OpResult.SUCCESS, orbInternalId, true);
			operationResult.rollbackAction = rollbackAction;
			operationResult.tranDate = tranDate;
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}
		
		return operationResult;
	}
}
