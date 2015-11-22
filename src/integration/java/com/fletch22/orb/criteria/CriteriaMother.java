package com.fletch22.orb.criteria;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.limitation.LimitationManager;
import com.fletch22.orb.query.CriteriaFactory;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class CriteriaMother {

	@Autowired
	CriteriaFactory criteriaFactory;

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	OrbManager orbManager;
	
	@Autowired
	LimitationManager limitationManager;
	
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

	public Criteria getCriteriaSample() {
		
		Criteria criteria = criteriaFactory.createInstance(getOrbType(), "foo");
		
		limitationManager.addDefaultLimitation(criteria);
		
		return criteria;
	}
}
