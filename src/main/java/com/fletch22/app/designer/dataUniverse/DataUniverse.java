package com.fletch22.app.designer.dataUniverse;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.appContainer.AppContainerChild;

public class DataUniverse extends Parent implements AppContainerChild {

	public static final String TYPE_LABEL = "DataUniverse";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(DataUniverse.ATTR_PARENT, ATTR_LABEL, ATTR_ORDINAL, ATTR_CHILDREN));

	public String label;

	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
