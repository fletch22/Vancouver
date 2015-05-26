package com.fletch22.orb.command.orbType;

public class DeleteOrbDto {

	public long orbInternalId;
	public boolean allowCascadingDeletes;
	
	public DeleteOrbDto(long orbInternalId, boolean allowCascadingDeletes) {
		this.orbInternalId = orbInternalId;
		this.allowCascadingDeletes = allowCascadingDeletes;
	}
}
