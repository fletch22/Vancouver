package com.fletch22.orb.attribute;


public interface OrbEventAware {
	
	public void handleAttributeRenameEvent(long orbTypeInternalId, String oldAttributeName, String newAttributeName);
	
	public void handleAttributeDeleteEvent(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies);
	
	public void handleTypeDeleteEvent(long orbTypeInternalId, boolean isDeleteDependencies);
	
	public void handleInstanceDeleteEvent(long orbInternalId, boolean isDeleteDependencies);
}
