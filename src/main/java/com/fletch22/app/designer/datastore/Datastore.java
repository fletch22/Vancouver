package com.fletch22.app.designer.datastore;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.dataUniverse.DataUniverseChild;

public class Datastore extends Parent implements DataUniverseChild {

	public static final String TYPE_LABEL = "Datastore";
	public static final String ATTR_LABEL = "label";
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(Datastore.ATTR_PARENT, ATTR_LABEL, ATTR_ORDINAL, ATTR_CHILDREN));

	public String label;

	@Override
	public String getTypeLabel() {
		return TYPE_LABEL;
	}
}
