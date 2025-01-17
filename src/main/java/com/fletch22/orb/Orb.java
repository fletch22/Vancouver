package com.fletch22.orb;

import java.util.LinkedHashMap;

import com.fletch22.orb.serialization.GsonSerializable;


public class Orb implements GsonSerializable {

	public static final long INTERNAL_ID_UNSET = -1;
	private LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
	private long orbInternalId = INTERNAL_ID_UNSET;
	private long orbTypeInternalId = OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET;
	
	public Orb() {
		// Do Nothing
	}
	
	public Orb(long internalId, long orbTypeInternalId, LinkedHashMap<String, String> userDefinedProperties) {
		this.orbInternalId = internalId;
		this.setOrbTypeInternalId(orbTypeInternalId);
		this.userDefinedProperties = userDefinedProperties;
	}

	public long getOrbInternalId() {
		return orbInternalId;
	}

	public void setOrbInternalId(long orbInteralId) {
		this.orbInternalId = orbInteralId;
	}

	public LinkedHashMap<String, String> getUserDefinedProperties() {
		return userDefinedProperties;
	}

	public void setUserDefinedProperties(LinkedHashMap<String, String> userDefinedProperties) {
		this.userDefinedProperties = userDefinedProperties;
	}

	public long getOrbTypeInternalId() {
		return orbTypeInternalId;
	}

	public void setOrbTypeInternalId(long orbTypeInternalId) {
		this.orbTypeInternalId = orbTypeInternalId;
	}
}
