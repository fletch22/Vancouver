package com.fletch22.processor;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.command.ActionSniffer;
import com.fletch22.command.CommandFactory;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;

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
 
	public void process(String action, BigDecimal tranDate) {
		
		String actionVerb = actionSniffer.getVerb(action);
		
		switch (actionVerb) {
			case CommandExpressor.ADD_ORB_TYPE:
				AddOrbTypeDto addOrbTypeDto = this.addOrbTypeCommand.fromJson(action);
				execute(addOrbTypeDto, tranDate);
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine json command type from '" + action + "'.");
		}
		
	}
	
	public void execute(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate) {
		
		orbTypeManager.createOrbType(addOrbTypeDto, tranDate);
		
	}
}
