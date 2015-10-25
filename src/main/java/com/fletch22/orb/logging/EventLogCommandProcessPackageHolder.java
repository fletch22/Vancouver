package com.fletch22.orb.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;

@Component
public class EventLogCommandProcessPackageHolder {
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	private CommandProcessActionPackage commandProcessActionPackage;
	
	public void cleanup() {
		commandProcessActionPackage = null;
	}
	
	public boolean hasInitialCommandActionBeenAdded() {
	
		boolean result = true;
		if (commandProcessActionPackage == null) {
			result = false;
		} else {
			result = commandProcessActionPackage.getAction() != null;
		}
		
		return result;
	}

	public CommandProcessActionPackage getCommandProcessActionPackage() {
		return commandProcessActionPackage;
	}

	public void setCommandProcessActionPackage(CommandProcessActionPackage commandProcessActionPackage) {
		this.commandProcessActionPackage = commandProcessActionPackage;
	}

	public void ensureInitialized() {
		if (commandProcessActionPackage == null) {
			commandProcessActionPackage = commandProcessActionPackageFactory.getInstanceForDirectInvocation();
		}
	}
}

