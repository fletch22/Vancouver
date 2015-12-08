package com.fletch22.orb.query;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;

@Component
public class QueryMother {
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	QueryManager queryManager;
	public Criteria getAggregateQuery() {
	
		String attributeName = "bar";
		
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>());
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName);
		
		String queryLabel = "fuzzyThings";
		Criteria criteria = new CriteriaStandard(orbType, queryLabel);
		
		CriteriaAggregate criteriaAgg = new CriteriaAggregate(orbType, "agg", attributeName);
		
		criteria.addAnd(Constraint.is(attributeName, Aggregate.UNIQUE, criteriaAgg));
		
		queryManager.addToCollection(criteria);
		
		return criteria;
	}
}
