package com.fletch22.orb.query;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.CriteriaImpl;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.ConstraintDetailsAggregate;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;

@Component
public class QueryMother {
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	CriteriaFactory criteriaFactory;
	
	@Autowired
	QueryManager queryManager;
	public CriteriaImpl getAggregateQuery() {
	
		String attributeName = "bar";
		
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>());
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName);
		
		String queryLabel = "fuzzyThings";
		CriteriaImpl criteria = criteriaFactory.createInstance(orbType, queryLabel);
		
		CriteriaImpl criteriaAgg = criteriaFactory.createInstance(orbType, "agg");
		
		criteria.addAnd(Constraint.is(attributeName, Aggregate.UNIQUE, criteriaAgg, attributeName));
		
		queryManager.addToCollection(criteria);
		
		return criteria;
	}
}
