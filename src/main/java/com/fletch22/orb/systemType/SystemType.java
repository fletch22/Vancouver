package com.fletch22.orb.systemType;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class SystemType {
	
	public static final String QUERY_INSTANCE_ATTRIBUTE_LABEL = "label";

	public static final SystemType QUERY = new SystemType(1, "query", new LinkedHashSet<String>(Arrays.asList(QUERY_INSTANCE_ATTRIBUTE_LABEL)));
	
	private long id;
	private String label;
	private LinkedHashSet<String> attributeHashSet;
	
	public SystemType(long id, String label, LinkedHashSet<String> attributeLinkHashSet) {
		this.id = id;
		this.label = label;
		this.attributeHashSet = attributeLinkHashSet;
	}

	public long getId() {
		return this.id;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public LinkedHashSet<String> getAttributeHashSet() {
		return this.attributeHashSet;
	}
}
