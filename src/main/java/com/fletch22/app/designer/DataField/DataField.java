package com.fletch22.app.designer.DataField;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;

public class DataField extends Parent {

	public static final String TYPE_LABEL = "DataField";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(DataField.ATTR_PARENT, ATTR_LABEL));
	
	public String label;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL; 
	}
}
