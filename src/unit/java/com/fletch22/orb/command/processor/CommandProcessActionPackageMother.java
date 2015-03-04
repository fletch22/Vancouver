package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class CommandProcessActionPackageMother {
	
	AddOrbTypeCommand addOrbTypeCommand;
	
	public static final String LABEL_GOOD = "foo";
	
	@Autowired
	CommandProcessActionPackageFactory factory;

	public CommandProcessActionPackage getGoodOne() {

		CommandProcessActionPackage commandProcessActionPackage = factory.getInstance(new StringBuilder("{\"fooName\":\"fooValue\"}"));
		commandProcessActionPackage.setTranDate(new BigDecimal(123))
		.setUndoActionBundle(new UndoActionBundle());
		
		return commandProcessActionPackage;
	}
}
