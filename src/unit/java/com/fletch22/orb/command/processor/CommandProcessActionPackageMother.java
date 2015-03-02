package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.rollback.UndoActionBundle;

public class CommandProcessActionPackageMother {
	
	AddOrbTypeCommand addOrbTypeCommand;
	
	public static final String LABEL_GOOD = "foo";

	public static CommandProcessActionPackage getGoodOne() {

		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		commandProcessActionPackage.setAction(new StringBuilder("{\"fooName\":\"fooValue\"}"))
		.setTranDate(new BigDecimal(123))
		.setUndoActionBundle(new UndoActionBundle());
		
		return commandProcessActionPackage;
	}
}
