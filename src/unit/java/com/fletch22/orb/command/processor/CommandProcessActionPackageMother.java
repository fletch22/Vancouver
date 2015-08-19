package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.orbType.AddBaseOrbTypeCommand;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.transaction.BeginTransactionCommand;
import com.fletch22.orb.command.transaction.CommitTransactionCommand;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class CommandProcessActionPackageMother {
	
	public static final String LABEL_GOOD = "foo";
	
	@Autowired
	CommandProcessActionPackageFactory factory;
	
	@Autowired
	BeginTransactionCommand beginTransactionCommand;
	
	@Autowired
	AddBaseOrbTypeCommand addBaseOrbTypeCommand;
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;
	
	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand;
	
	@Autowired
	CommitTransactionCommand commitTransactionCommand;

	public CommandProcessActionPackage getGoodOne() {

		CommandProcessActionPackage commandProcessActionPackage = factory.getInstance(new StringBuilder("{\"fooName\":\"fooValue\"}"));
		commandProcessActionPackage.setTranDate(new BigDecimal(123))
		.setUndoActionBundle(new UndoActionBundle());
		
		return commandProcessActionPackage;
	}
	
	public CommandProcessActionPackage getGoodOne(Class<?> commandClazz) {
		
		StringBuilder action;
		if (commandClazz == BeginTransactionCommand.class) {
			action = this.beginTransactionCommand.toJson();
		} else if (commandClazz == AddBaseOrbTypeCommand.class) {
			action = this.addBaseOrbTypeCommand.toJson("foo");
		} else if (commandClazz == AddOrbTypeCommand.class) {
			action = this.addBaseOrbTypeCommand.toJson("foo");
		} else if (commandClazz == DeleteOrbTypeCommand.class) {
			action = this.deleteOrbTypeCommand.toJson(123, true);
		} else if (commandClazz == CommitTransactionCommand.class) {
			action = this.commitTransactionCommand.toJson(new BigDecimal(123));
		} else {
			String name = (null == commandClazz) ? "<null>": commandClazz.getName(); 
			throw new RuntimeException(String.format("Encountered problem with command class passed to method. Did not recognize %s", name));
		}
		
		CommandProcessActionPackage commandProcessActionPackage = factory.getInstance(action);
		commandProcessActionPackage.setTranDate(new BigDecimal(123))
		.setUndoActionBundle(new UndoActionBundle());
		
		return commandProcessActionPackage;
	}
}
