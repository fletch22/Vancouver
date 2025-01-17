package com.fletch22.orb.command.orbType.dto;

import com.fletch22.command.dto.Dto;
import com.fletch22.orb.OrbTypeConstants;

public class AddOrbTypeDto implements Dto {
	
	public String label;
	public long orbTypeInternalId = OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET;
	
	public AddOrbTypeDto(String label, int orbTypeInternalId) {
		 this.label = label;
		 this.orbTypeInternalId = orbTypeInternalId;
	}
	
}
