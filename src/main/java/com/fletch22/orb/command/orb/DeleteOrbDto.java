package com.fletch22.orb.command.orb;

public class DeleteOrbDto {

	public long orbTypeInternalId;
	public boolean allowCascadingDeletes;
	
	public DeleteOrbDto(long orbInternalId, boolean allowCascadingDeletes) {
		this.orbTypeInternalId = orbInternalId;
		this.allowCascadingDeletes = allowCascadingDeletes;
	}
}
