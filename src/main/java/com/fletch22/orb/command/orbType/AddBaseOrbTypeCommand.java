package com.fletch22.orb.command.orbType;

import com.fletch22.orb.OrbTypeManager;

public class AddBaseOrbTypeCommand extends AddOrbTypeCommand {

	@Override
	public StringBuilder toJson(String orbLabel) {
		return this.toJson(orbLabel, OrbTypeManager.ORBTYPE_BASETYPE_ID);
	}
}
