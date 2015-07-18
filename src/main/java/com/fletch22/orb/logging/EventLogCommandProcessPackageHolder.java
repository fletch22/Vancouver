package com.fletch22.orb.logging;

import org.springframework.stereotype.Component;

import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;

@Component
public class EventLogCommandProcessPackageHolder {
	public CommandProcessActionPackage commandProcessActionPackage;
	
	public void clear() {
		commandProcessActionPackage = null;
	}
}
