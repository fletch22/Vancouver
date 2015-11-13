package com.fletch22.orb.attribute;


public interface OrbTypeEventAware {
	
	public void handleAttributeRenameEvent(long orbTypeInternalId, String oldAttributeName, String newAttributeName);
	
	public void handleAttributeDeleteEvent(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies);
	
	public void handleTypeDeleteEvent(long orbTypeInternalId, boolean isDeleteDependencies);
}
