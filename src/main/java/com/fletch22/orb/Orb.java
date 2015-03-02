package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class Orb {

	private static final long UNSET = -1;
	private Map<String, String> userDefinedProperties = new HashMap<String, String>();
	private long orbInteralId = UNSET;
	private BigDecimal tranDate;
	
	public Orb() {
		// Do Nothing
	}
	
	public Orb(long internalId, BigDecimal tranDate, Map<String, String> userDefinedProperties) {
		this.orbInteralId = internalId;
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

	public Map<String, String> getUserDefinedProperties() {
		return userDefinedProperties;
	}

	public void setUserDefinedProperties(Map<String, String> userDefinedProperties) {
		this.userDefinedProperties = userDefinedProperties;
	}
}
