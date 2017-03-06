package com.fletch22.app.designer.DataModel;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;

public class DataModel extends Parent {

	public static final String TYPE_LABEL = "DataModel";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(DataModel.ATTR_PARENT, ATTR_LABEL, ATTR_CHILDREN));
	
	public String label;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL; 
	}
}
