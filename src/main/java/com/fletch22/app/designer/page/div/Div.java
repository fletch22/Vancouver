package com.fletch22.app.designer.page.div;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;

public class Div extends OrbBasedComponent {

	public static final String TYPE_LABEL = "Div";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_LABEL, ATTR_CHILDREN));
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
