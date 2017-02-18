package com.fletch22.app.designer.ddl;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;

public class DropDownListbox extends OrbBasedComponent implements DropDownListboxChild {

	public static final String TYPE_LABEL = "DropDownListbox";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_DATA_SOURCE_NAME = "dataSourceName";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(DropDownListbox.ATTR_PARENT, ATTR_NAME, ATTR_DATA_SOURCE_NAME, ATTR_STYLE));
	
	public String style;
	public String name;
	public String dataSourceName;
	
	@Override
	public String getTypeLabel() {
		return TYPE_LABEL; 
	}
}
