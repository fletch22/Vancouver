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
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.orb.query.Constraint;
import com.fletch22.orb.query.CriteriaFactory;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.QueryManager;

@Component
public class TestDataWithReferences {
	
	static Logger logger = LoggerFactory.getLogger(TestDataWithReferences.class);
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	CriteriaFactory criteriaFactory;
	
	@Autowired
	QueryManager queryManager;
	
	@Autowired
	ReferenceUtil referenceUtil;
	
	@Autowired
	Cache cache;
	
	public static final String ATTRIBUTE_COLOR = "color";
	public static final String ATTRIBUTE_SPEED = "speed";
	public static final String ATTRIBUTE_FLAVOR = "flavor";
	
	public static final String ATTRIBUTE_GREEN = "green";
	public static final String ATTRIBUTE_ORANGE = "orange";
	public static final String ATTRIBUTE_RED = "red";
	
	public static final int NUMBER_GREEN = 40;
	public static final int NUMBER_ORANGE = 10;
	public static final int NUMBER_RED = 60;
	
	public static final int TOTAL_NUMBER_INSTANCES = NUMBER_GREEN + NUMBER_ORANGE + NUMBER_RED;
	
	long orbTypeInternalId;

	public Orb loadTestData(int numberOfInstances) {

		LinkedHashSet<String> customFields = new LinkedHashSet<String>();
		customFields.add(ATTRIBUTE_COLOR);
		customFields.add(ATTRIBUTE_SPEED);
		customFields.add(ATTRIBUTE_FLAVOR);
		
		this.orbTypeInternalId = orbTypeManager.createOrbType("foo", customFields);
		
		Orb orbTarget = orbManager.createOrb(orbTypeInternalId);
		orbManager.setAttribute(orbTarget.getOrbInternalId(), ATTRIBUTE_COLOR, "green");
		
		String reference = referenceUtil.composeReference(orbTarget.getOrbInternalId(), ATTRIBUTE_COLOR);
		
		logger.debug("Ref: {}", reference);
		
		for (int i = 0; i < numberOfInstances; i++) {
			Orb orb = orbManager.createOrb(orbTypeInternalId);
			orbManager.setAttribute(orb.getOrbInternalId(), ATTRIBUTE_COLOR, reference);
		}
		
		return orbTarget;
	}
	
	public long addSimpleCriteria() {
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		Criteria criteria = criteriaFactory.createInstance(orbType, "foo");
		
		Constraint constraint = Constraint.eq(ATTRIBUTE_COLOR, "green");
		
		criteria.addAnd(constraint);
		
		long orbInternalIdQuery = queryManager.create(criteria);
		
		return orbInternalIdQuery;
	}
	
}
