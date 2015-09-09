package com.fletch22.orb.query;

public class ConstraintDetails extends Constraint {
	
	RelationshipOperator relationshipOperator;
	String attributeName;
	String operativeValue;
	
	@Override
	public Constraint[] getConstraints() {
		
		Constraint[] constraintArray = new Constraint[1];
		
		constraintArray[0] = this;
		
		return constraintArray;
	}
}
