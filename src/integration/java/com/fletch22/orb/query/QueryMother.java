package com.fletch22.orb.query;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaAggregate;
import com.fletch22.orb.query.criteria.CriteriaStandard;

@Component
public class QueryMother {
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	QueryManager queryManager;
	
	public Criteria getSimpleAggregateQuery() {
	
		String attributeName = "bar";
		String attributeName2 = "bar2";
		
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>());
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName);
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName2);
		
		String queryLabel = "fuzzyThings";
		Criteria criteria = new CriteriaStandard(orbTypeInternalId, queryLabel);
		
		String[] attributeNames = { attributeName };
		CriteriaAggregate criteriaAgg = new CriteriaAggregate(orbTypeInternalId, "agg", attributeNames);
		
		criteria.addAnd(Constraint.are(attributeNames, Aggregate.AMONGST_UNIQUE, criteriaAgg));
		
		queryManager.addToCollection(criteria);
		
		return criteria;
	}
	
	public Criteria getComplexAggregateQuery1() {
		
		String attributeName = "bar";
		String attributeName2 = "banana";
		
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>());
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName);
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName2);
		
		String queryLabel = "fuzzyThings";
		Criteria criteria = new CriteriaStandard(orbTypeInternalId, queryLabel);
		
		String[] attributeNames = { attributeName2 };
		CriteriaAggregate criteriaAgg = new CriteriaAggregate(orbTypeInternalId, "agg", attributeNames);
		
		criteria.addAnd(Constraint.are(attributeNames, Aggregate.AMONGST_UNIQUE, criteriaAgg));
		
		queryManager.addToCollection(criteria);
		
		return criteria;
	}
}
