package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;

public class ConstraintDetailsAggregate extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public Aggregate aggregate;
	public Criteria criteriaForAggregation;
	public String aggregationAttributeName;
	
	@Override
	public Constraint[] getConstraints() {
		
		Constraint[] constraintArray = new Constraint[1];
		
		constraintArray[0] = this;
		
		return constraintArray;
	}

	@Override
	public String getAttributeName() {
		return this.attributeName;
	}
	
	@Override
	public RelationshipOperator getRelationshipOperator() {
		return this.relationshipOperator;
	}

	
}
