package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.command.ActionSniffer;
import com.fletch22.command.CommandFactory;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.OrbTypeManager;
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
 
	public OperationResult processAction(String action, BigDecimal tranDate) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		String actionVerb = actionSniffer.getVerb(action);
		
		switch (actionVerb) {
			case CommandExpressor.ADD_ORB_TYPE:
				AddOrbTypeDto addOrbTypeDto = this.addOrbTypeCommand.fromJson(action);
				operationResult = execute(addOrbTypeDto, tranDate);
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine json command type from '" + action + "'.");
		}
		
		operationResult.internalIdAfterOperation = this.internalIdGenerator.getCurrentId();
		
		return operationResult;
	}
	
	public OperationResult execute(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate) {
		
		OperationResult operationResult = OperationResult.IN_THE_MIDDLE;
		operationResult.internalIdBeforeOperation = this.internalIdGenerator.getCurrentId();
		
		try {
			long orbInternalId = orbTypeManager.createOrbType(addOrbTypeDto, tranDate);
			
			operationResult = new OperationResult(OpResult.SUCCESS, orbInternalId, true);
		} catch (Exception e) {
			operationResult = new OperationResult(OpResult.FAILURE, e);
		}
		
		return operationResult;
	}
}
