package com.fletch22.orb.query.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaAggregate;
import com.fletch22.orb.query.criteria.CriteriaStandard;
import com.fletch22.util.RandomUtil;

public class CriteriaBuilder {
	
	Logger logger = LoggerFactory.getLogger(CriteriaBuilder.class);
	
	CriteriaStandard criteriaStandard;
	RandomUtil randomUtil;
	
	public CriteriaBuilder(long orbTypeInternalId) {
		randomUtil = Fletch22ApplicationContext.getApplicationContext().getBean(RandomUtil.class);
		criteriaStandard = new CriteriaStandard(orbTypeInternalId, randomUtil.getRandomUuidString());
	}
	
	public CriteriaBuilder addAmongstUniqueConstraint(long orbTypeInternalId, String[] attributeNameToBeUnique) {
		
		CriteriaAggregate criteriaForAggregation = new CriteriaAggregate(orbTypeInternalId, randomUtil.getRandomUuidString(), attributeNameToBeUnique);
		criteriaStandard.addAnd(Constraint.are(attributeNameToBeUnique, Aggregate.AMONGST_UNIQUE, criteriaForAggregation));
		
		return this;
	}
	
	public CriteriaBuilder addNotAmongstUniqueConstraint(long orbTypeInternalId, String[] attributeNameToBeUnique) {
		
		CriteriaAggregate criteriaForAggregation = new CriteriaAggregate(orbTypeInternalId, randomUtil.getRandomUuidString(), attributeNameToBeUnique);
		criteriaStandard.addAnd(Constraint.are(attributeNameToBeUnique, Aggregate.NOT_AMONGST_UNIQUE, criteriaForAggregation));
		
		return this;
	}

	public Criteria build() {
		return criteriaStandard;
	}
}
