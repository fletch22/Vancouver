package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.TranDateGenerator;

@Component
public class CommandProcessActionPackageFactory {
	
	@Autowired
	TranDateGenerator tranDateGenerator;

	public CommandProcessActionPackage getInstance(StringBuilder action) {
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		
		commandProcessActionPackage.action = action;
		commandProcessActionPackage.tranDate = tranDateGenerator.getTranDate();
		
		return commandProcessActionPackage;
	}
	
	public CommandProcessActionPackage getInstance(StringBuilder action, BigDecimal tranDate) {
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		
		commandProcessActionPackage.action = action;
		commandProcessActionPackage.tranDate = tranDate;
		
		return commandProcessActionPackage;
	}
	
	public CommandProcessActionPackage getInstance(StringBuilder action, BigDecimal tranDate, OperationResult operationResult) {
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		
		commandProcessActionPackage.action = action;
		commandProcessActionPackage.tranDate = tranDate;
		commandProcessActionPackage.operationResult = operationResult;
		
		return commandProcessActionPackage;
	}
}
