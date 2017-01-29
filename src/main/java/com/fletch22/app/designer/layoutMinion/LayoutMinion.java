package com.fletch22.app.designer.layoutMinion;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.page.Page;

public class LayoutMinion extends Parent implements LayoutMinionChild {
	
	public static final String TYPE_LABEL = "LayoutMinion";
	public static final String ATTR_HEIGHT = "height";
	public static final String ATTR_WIDTH = "width";
	public static final String ATTR_X = "x";
	public static final String ATTR_Y = "y";
	public static final String ATTR_KEY = "key";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(Page.ATTR_PARENT, ATTR_HEIGHT, ATTR_WIDTH, ATTR_X, ATTR_Y, ATTR_KEY, ATTR_CHILDREN));
	
	public String height;
	public String width;
	public String x;
	public String y;
	public String key;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}



