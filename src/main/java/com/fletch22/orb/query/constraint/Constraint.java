package com.fletch22.orb.query.constraint;

import java.util.List;

import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;

public abstract class Constraint {
	public abstract Constraint[] getConstraints();
	
	public Constraint() {}
	
	public static Constraint eq(String attributeName, String operativeValue) {
		ConstraintDetailsSingleValue constraintDetails = new ConstraintDetailsSingleValue();
		
		constraintDetails.relationshipOperator = RelationshipOperator.EQUALS;
		constraintDetails.attributeName = attributeName;
		constraintDetails.operativeValue = operativeValue;
		
		return constraintDetails;
	}
	
	public static Constraint in(String attributeName, List<String> operativeList) {
		ConstraintDetailsList constraintDetailsList = new ConstraintDetailsList();
		
		constraintDetailsList.relationshipOperator = RelationshipOperator.IN;
		constraintDetailsList.attributeName = attributeName;
		constraintDetailsList.operativeValueList = operativeList;
		
		return constraintDetailsList;
	}
	
	public static Constraint is(String attributeName, Aggregate aggregate, Criteria criteriaForAggregation, String aggregateAttributeName) {
		ConstraintDetailsAggregate constraintDetailsAggregate = new ConstraintDetailsAggregate();
		
		constraintDetailsAggregate.relationshipOperator = RelationshipOperator.IS;
		constraintDetailsAggregate.attributeName = attributeName;
		constraintDetailsAggregate.aggregate = aggregate;
		constraintDetailsAggregate.aggregationAttributeName = aggregateAttributeName;
		constraintDetailsAggregate.criteriaForAggregation = criteriaForAggregation;
		
		return constraintDetailsAggregate;
	}
	
	public void accept(ConstraintVisitor constraintVisitor) {
		
	}
}
