package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.RelationshipOperator;

public class ConstraintDetailsSingleValue extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public String operativeValue;
	
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

	public String getOperativeValue() {
		return this.operativeValue;
	}
}
