package com.fletch22.orb.query;

public abstract class Constraint {
	public abstract Constraint[] getConstraints();
	
	public static Constraint eq(String attributeName, String operativeValue) {
		ConstraintDetails constraintDetails = new ConstraintDetails();
		
		constraintDetails.relationshipOperator = RelationshipOperator.EQUALS;
		constraintDetails.attributeName = attributeName;
		constraintDetails.operativeValue = operativeValue;
		
		return constraintDetails;
	}
}
