package com.fletch22.app.designer.submit;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;

public class ButtonSubmit extends Parent {

	public static final String TYPE_LABEL = "ButtonSubmit";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ButtonSubmit.ATTR_PARENT, ATTR_NAME, ATTR_LABEL, ATTR_STYLE));

	public String style;
	public String name;
	public String label;

	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
