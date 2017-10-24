package com.fletch22.app.designer.submit;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;

public class ButtonSubmit extends OrbBasedComponent {

	public static final String TYPE_LABEL = "ButtonSubmit";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_ELEMENT_ID = "elementId";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ButtonSubmit.ATTR_PARENT, ATTR_ELEMENT_ID, ATTR_LABEL, ATTR_STYLE, ATTR_ORDINAL));

	public String style;
	public String elementId;
	public String label;

	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
