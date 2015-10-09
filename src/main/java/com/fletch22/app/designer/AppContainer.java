package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class AppContainer {
	
	public static final String TYPE_LABEL = "AppContainer";
	
	public static final String ATTR_APPS = "apps";
	public static final String ATTR_LABEL = "label";
	public static final Set<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_APPS, ATTR_LABEL));

	public ArrayList<App> appList;
	public String label;
	public long id;
}
