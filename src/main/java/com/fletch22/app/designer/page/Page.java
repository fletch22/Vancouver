package com.fletch22.app.designer.page;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.webFolder.WebFolderChild;

public class Page extends Parent implements WebFolderChild {
	
	public static final String TYPE_LABEL = "Page";
	public static final String ATTR_PAGE_NAME = "pageName";
	public static final String ATTR_STYLE = "style";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(Page.ATTR_PARENT, ATTR_PAGE_NAME, ATTR_CHILDREN, ATTR_ORDINAL, ATTR_STYLE));
	
	public String pageName;
	public String style;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
