package com.fletch22.orb.command.orbType.dto;

import com.fletch22.command.dto.Dto;

public class UpdateOrbTypeLabelDto implements Dto {
	String label;
	int orbTypeInternalId;
	
	public UpdateOrbTypeLabelDto(String label, int orbTypeInternalId) {
		this.label = label;
		this.orbTypeInternalId = orbTypeInternalId;
	}
}
