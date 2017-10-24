package com.fletch22.app.designer.page.body;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.page.PageChild;

public class Body extends Parent implements PageChild {

	public static final String TYPE_LABEL = "Body";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(Body.ATTR_PARENT, ATTR_LABEL, ATTR_CHILDREN, ATTR_ORDINAL));
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
