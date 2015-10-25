package com.fletch22.app.designer;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class AppContainer extends OrbBasedComponent {
	
	public static final String TYPE_LABEL = "AppContainer";
	
	public static final String ATTR_APPS = "apps";
	public static final String ATTR_LABEL = "label";

	static {
		ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_APPS, ATTR_LABEL));
	}

	public String label;

	public void setLabel(String appContainerLabel) {
		this.label = appContainerLabel;
	}
}
