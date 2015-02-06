package com.fletch22.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;

@Component
public class CommandFactory {

	@Autowired
	ActionSniffer actionSniffer;
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;
	
	public JsonCommand getJsonCommand(StringBuilder action) {
		
		String actionVerb = actionSniffer.getVerb(action);
		
		JsonCommand jsonCommand = null;
		switch (actionVerb) {
			case CommandExpressor.ADD_ORB_TYPE:
				jsonCommand = this.addOrbTypeCommand;
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine json command type from '" + action + "'.");
		}
		
		return jsonCommand;
	}
}
