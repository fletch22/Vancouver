package com.fletch22.orb.command.orbType;

import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbTypeManager;

@Component
public class AddBaseOrbTypeCommand extends AddOrbTypeCommand {

	@Override
	public StringBuilder toJson(String orbLabel) {
		return this.toJson(orbLabel, OrbTypeManager.ORBTYPE_BASETYPE_ID);
	}
}
