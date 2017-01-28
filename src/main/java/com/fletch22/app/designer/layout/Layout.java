package com.fletch22.app.designer.layout;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.page.Page;

public class Layout extends Parent implements LayoutChild {
	
	public static final String TYPE_LABEL = "Layout";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(Page.ATTR_PARENT, ATTR_CHILDREN));
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
