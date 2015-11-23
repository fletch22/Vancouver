package com.fletch22.orb.criteria;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.orb.query.CriteriaFactory;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class DefLimitationMother {

	@Autowired
	CriteriaFactory criteriaFactory;

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	OrbManager orbManager;
	
	@Autowired
	DefLimitationManager defLimitationManager;
	
	public static final String ATTRIBUTE_BAR = "bar";

	OrbType orbType = null;

	public OrbType getOrbType() {
		if (orbType == null) {
			
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			
			set.add(ATTRIBUTE_BAR);
			
			long orbTypeInternalId = orbTypeManager.createOrbType("fooTypeForSampleCriteria", set);
			orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		}
		return orbType;
	}

	public Criteria createAndAddCriteriaSimple() {
		
		Criteria criteria = criteriaFactory.createInstance(getOrbType(), "foo");
		
		defLimitationManager.addToCollection(criteria);
		
		return criteria;
	}
}
