package com.fletch22.orb.command.orbType.dto;

public class UpdateOrbTypeLabelDto {
	String label;
	int orbTypeInternalId;
	
	public UpdateOrbTypeLabelDto(String label, int orbTypeInternalId) {
		this.label = label;
		this.orbTypeInternalId = orbTypeInternalId;
	}
}
