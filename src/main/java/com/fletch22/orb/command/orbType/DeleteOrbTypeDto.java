package com.fletch22.orb.command.orbType;

public class DeleteOrbTypeDto {

	public long orbTypeInternalId;
	public boolean allowCascadingDeletes;
	
	public DeleteOrbTypeDto(long orbInternalId, boolean allowCascadingDeletes) {
		this.orbTypeInternalId = orbInternalId;
		this.allowCascadingDeletes = allowCascadingDeletes;
	}
}
