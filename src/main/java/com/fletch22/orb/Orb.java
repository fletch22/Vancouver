package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import com.fletch22.orb.serialization.JsonSerializable;


public class Orb implements JsonSerializable {

	public static final long INTERNAL_ID_UNSET = -1;
	private LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
	private long orbInternalId = INTERNAL_ID_UNSET;
	private long orbTypeInternalId = OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET;
	private BigDecimal tranDate;
	
	public Orb() {
		// Do Nothing
	}
	
	public Orb(long internalId, long orbTypeInternalId, BigDecimal tranDate, LinkedHashMap<String, String> userDefinedProperties) {
		this.orbInternalId = internalId;
		this.setOrbTypeInternalId(orbTypeInternalId);
		this.tranDate = tranDate;
		this.userDefinedProperties = userDefinedProperties;
	}

	public long getOrbInternalId() {
		return orbInternalId;
	}

	public void setOrbInternalId(long orbInteralId) {
		this.orbInternalId = orbInteralId;
	}

	public BigDecimal getTranDate() {
		return tranDate;
	}

	public void setTranDate(BigDecimal tranDate) {
		this.tranDate = tranDate;
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
