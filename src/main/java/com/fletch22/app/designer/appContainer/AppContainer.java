package com.fletch22.app.designer.appContainer;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.Parent;

public class AppContainer extends OrbBasedComponent implements Parent {
	
	public static final String TYPE_LABEL = "AppContainer";
	
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_PARENT, ATTR_CHILDREN, ATTR_LABEL)); 

	public String label;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
