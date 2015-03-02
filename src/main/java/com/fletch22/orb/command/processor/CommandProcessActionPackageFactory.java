package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class CommandProcessActionPackageFactory {
	
	@Autowired
	TranDateGenerator tranDateGenerator;

//	public CommandProcessActionPackage getInstance(StringBuilder action) {
//		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
//		
//		commandProcessActionPackage.setAction(action)
//		.setTranDate(tranDateGenerator.getTranDate())
//		.setUndoActionBundle(new UndoActionBundle());
//		
//		return commandProcessActionPackage;
//	}
//	
//	public CommandProcessActionPackage getInstance(StringBuilder action, BigDecimal tranDate) {
//		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
//		
//		commandProcessActionPackage.setAction(action)
//		.setTranDate(tranDate)
//		.setUndoActionBundle(new UndoActionBundle());
//		
//		return commandProcessActionPackage;
//	}
}
