package com.fletch22.orb.command.orbType;

import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbTypeConstants;

@Component
public class AddBaseOrbTypeCommand extends AddOrbTypeCommand {

	@Override
	public StringBuilder toJson(String orbLabel) {
		return this.toJson(orbLabel, OrbTypeConstants.ORBTYPE_BASETYPE_ID);
	}
}
