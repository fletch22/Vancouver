package com.fletch22.app.designer.app;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;

public class App extends OrbBasedComponent {
	
	public static final String TYPE_LABEL = "App";
	public static final String ATTR_LABEL = "label";
	public static final String ATTR_WEBSITES = "website";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_LABEL, ATTR_WEBSITES));
	
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
