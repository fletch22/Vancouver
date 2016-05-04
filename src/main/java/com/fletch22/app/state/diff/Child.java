package com.fletch22.app.state.diff;

import java.util.HashMap;
import java.util.Map;

public class Child {
	public long parentId;
	public String typeLabel;
	public Map<String, String> props = new HashMap<String, String>();
	
	public Child(String type, Map<String, String> properties, long parentId) {
		this.parentId = parentId;
		this.typeLabel = type;
		this.props = properties;
	}
}