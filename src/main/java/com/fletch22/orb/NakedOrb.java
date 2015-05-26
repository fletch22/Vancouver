package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fletch22.util.BigDecimalForDataStorageUtil;

public class NakedOrb {
	
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_TYPE_ID = "typeId";
	private static final String ATTRIBUTE_LABEL = "label";
	private static final String ATTRIBUTE_TRAN_DATE = "tranDate";

	Map<String, String> properties = new HashMap<String, String>();
	
	BigDecimalForDataStorageUtil bigDecimalForDataStorageUtil = new BigDecimalForDataStorageUtil();
	
	public NakedOrb(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public NakedOrb(long id, long typeId, String label, BigDecimal tranDate) { 
		properties.put(ATTRIBUTE_ID, String.valueOf(id));
		properties.put(ATTRIBUTE_TYPE_ID, String.valueOf(typeId));
		properties.put(ATTRIBUTE_LABEL, label);
		properties.put(ATTRIBUTE_TRAN_DATE, this.bigDecimalForDataStorageUtil.convertForDataStorage(tranDate));
	}
	
	public NakedOrb(long id, long typeId, BigDecimal tranDate) { 
		this(id, typeId, null, tranDate);
	}
	
	public String getLabel() {
		return properties.get(ATTRIBUTE_LABEL);
	}
	
	public void setLabel(String label) {
		this.properties.put(ATTRIBUTE_LABEL, label); 
	}
	
	public String getOrbInternalId() {
		return properties.get(ATTRIBUTE_ID);
	}
	
	public void setId(long id) {
		this.properties.put(ATTRIBUTE_ID, String.valueOf(id)); 
	}
	
	public String getOrbTypeInternalId() {
		return this.properties.get(ATTRIBUTE_TYPE_ID);
	}
	
	public void setOrbTypeInternalId(long typeId) {
		this.properties.put(ATTRIBUTE_TYPE_ID, String.valueOf(typeId));
	}
	
	public String getTranDate() {
		return properties.get(ATTRIBUTE_TRAN_DATE);
	}
	
	public Map<String, String> expressAllProperties() {
		return this.properties;
	}
	
	public Map<String, String> getUserDefinedProperties() {
		Map<String, String> userDefined = new HashMap<String, String>(this.properties);
		userDefined.remove(ATTRIBUTE_ID);
		userDefined.remove(ATTRIBUTE_TYPE_ID);
		userDefined.remove(ATTRIBUTE_LABEL);
		userDefined.remove(ATTRIBUTE_TRAN_DATE);
		return userDefined;
	}
}
