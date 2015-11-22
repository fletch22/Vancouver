package com.fletch22.orb.test.data;

import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.Constraint;
import com.fletch22.orb.query.CriteriaFactory;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class TestDataSimple {
	
	Logger logger = LoggerFactory.getLogger(TestDataSimple.class);
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	CriteriaFactory criteriaFactory;
	
	@Autowired
	QueryManager queryManager;
	
	public static final String ATTRIBUTE_COLOR = "color";
	public static final String ATTRIBUTE_GREEN = "green";
	public static final String ATTRIBUTE_ORANGE = "orange";
	public static final String ATTRIBUTE_RED = "red";
	
	private static final int MULTIPLIER = 1;
	public static final int NUMBER_GREEN = 40 * MULTIPLIER;
	public static final int NUMBER_ORANGE = 10 * MULTIPLIER;
	public static final int NUMBER_RED = 60 * MULTIPLIER;
	
	public static final int TOTAL_NUMBER_INSTANCES = NUMBER_GREEN + NUMBER_ORANGE + NUMBER_RED;
	
	long orbTypeInternalId;

	public long loadTestData() {

		LinkedHashSet<String> customFields = new LinkedHashSet<String>();

		customFields.add(ATTRIBUTE_COLOR);
		customFields.add("size");
		customFields.add("speed");

		orbTypeInternalId = orbTypeManager.createOrbType("foo", customFields);

		setNumberInstancesToColor(NUMBER_RED, orbTypeInternalId, ATTRIBUTE_RED);
		setNumberInstancesToColor(NUMBER_ORANGE, orbTypeInternalId, ATTRIBUTE_ORANGE);
		setNumberInstancesToColor(NUMBER_GREEN, orbTypeInternalId, ATTRIBUTE_GREEN);
		
		return orbTypeInternalId;
	}
	
	public long addSimpleCriteria() {
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		Criteria criteria = criteriaFactory.createInstance(orbType, "foo");
		criteria.addAnd(Constraint.eq(ATTRIBUTE_COLOR, ATTRIBUTE_GREEN));
		
		long orbInternalIdQuery = queryManager.addToCollection(criteria);
		
		return orbInternalIdQuery;
	}
	
	private void setNumberInstancesToColor(int numInstances, long orbTypeInternalId, String color) {
		
		for (int i = 0; i < numInstances; i++) {
			
			Orb orb = orbManager.createOrb(orbTypeInternalId);
			orbManager.setAttribute(orb.getOrbInternalId(), ATTRIBUTE_COLOR, color);
		}
	}
}
