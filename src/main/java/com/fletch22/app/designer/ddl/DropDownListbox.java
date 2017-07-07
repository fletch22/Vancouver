package com.fletch22.app.designer.ddl;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.OrbBasedComponent;

public class DropDownListbox extends OrbBasedComponent implements DropDownListboxChild {

	public static final String TYPE_LABEL = "DropDownListbox";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_ELEMENT_ID = "elementId";
	public static final String ATTR_DATASTORE_ID = "dataStoreId";
	public static final String ATTR_DATAMODEL_ID = "dataModelId";
	public static final String ATTR_VALUE_FIELD_NAME = "dataValueId";
	public static final String ATTR_TEXT_FIELD_NAME = "dataTextId";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(DropDownListbox.ATTR_PARENT, ATTR_ELEMENT_ID, ATTR_DATASTORE_ID, ATTR_DATAMODEL_ID,
			ATTR_VALUE_FIELD_NAME, ATTR_TEXT_FIELD_NAME, ATTR_STYLE));

	public String style;
	public String elementId;
	public String dataStoreId;
	public String dataModelId;
	public String dataValueId;
	public String dataTextId;

	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
