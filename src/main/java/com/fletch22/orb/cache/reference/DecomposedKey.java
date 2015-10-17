package com.fletch22.orb.cache.reference;

public class DecomposedKey {
	private long orbInternalId;
	private String attributeName = null;
	
	public DecomposedKey(long orbInternalId, String attributeName) {
		this.orbInternalId = orbInternalId;
		this.attributeName = attributeName;
	}
	
	public DecomposedKey(long orbInternalId) {
		this.orbInternalId = orbInternalId;
	}
	
	public long getOrbInternalId() {
		return orbInternalId;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public boolean isKeyPointingToAttribute() {
		return attributeName != null;
	}
}
