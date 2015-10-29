package com.fletch22.app.designer;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class Website extends OrbBasedComponent  {
	
	public static final String TYPE_LABEL = "Website";
	public static final String ATTR_LABEL = "label";
	public static final String ATTR_WEB_SECTION = "web_section";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_LABEL, ATTR_WEB_SECTION)); 
}
