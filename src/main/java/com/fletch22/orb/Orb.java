package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Orb {

	private static final long UNSET = -1;
	private LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
	private long orbInteralId = UNSET;
	private long orbTypeInternalId = UNSET;
	private BigDecimal tranDate;
	
	public Orb() {
		// Do Nothing
	}
	
	public Orb(long internalId, long orbTypeInternalId, BigDecimal tranDate, LinkedHashMap<String, String> userDefinedProperties) {
		this.orbInteralId = internalId;
		this.setOrbTypeInternalId(orbTypeInternalId);
		this.tranDate = tranDate;
		this.userDefinedProperties = userDefinedProperties;
	}

	public long getOrbInteralId() {
		return orbInteralId;
	}

	public void setOrbInteralId(long orbInteralId) {
		this.orbInteralId = orbInteralId;
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
