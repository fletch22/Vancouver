package com.fletch22.app.designer.page;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.webFolder.WebFolderChild;

public class Page extends OrbBasedComponent implements WebFolderChild, Parent {
	
	public static final String TYPE_LABEL = "Page";
	public static final String ATTR_PAGE_NAME = "pageName";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_PAGE_NAME, ATTR_CHILDREN));
	
	public String pageName;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
