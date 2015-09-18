package com.fletch22.orb.test.data;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;

@Component
public class TestDataSimple {
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	public static final String ATTRIBUTE_COLOR = "color";
	public static final String ATTRIBUTE_GREEN = "green";
	public static final String ATTRIBUTE_ORANGE = "orange";
	public static final String ATTRIBUTE_RED = "red";

	public long loadTestData() {

		LinkedHashSet<String> customFields = new LinkedHashSet<String>();

		customFields.add(ATTRIBUTE_COLOR);
		customFields.add("size");
		customFields.add("speed");

		long orbTypeInternalId = orbTypeManager.createOrbType("foo", customFields);

		setNumberInstancesToColor(60, orbTypeInternalId, "red");
		setNumberInstancesToColor(10, orbTypeInternalId, "orange");
		setNumberInstancesToColor(40, orbTypeInternalId, "green");
		
		return orbTypeInternalId;
	}
	
	private void setNumberInstancesToColor(int numInstances, long orbTypeInternalId, String color) {
		
		for (int i = 0; i < numInstances; i++) {
			Orb orb = orbManager.createOrb(orbTypeInternalId);
			orbManager.setAttribute(orb.getOrbInternalId(), ATTRIBUTE_COLOR, color);
		}
	}
}
