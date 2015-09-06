package com.fletch22.orb.query;

public abstract class Constraint {
	public abstract Constraint[] getConstraints();
	
	public static Constraint eq(LogicalOperator logicalOperator, String attributeName, String operativeValue) {
		ConstraintDetails constraintDetails = new ConstraintDetails();
		
		constraintDetails.logicalOperator = logicalOperator;
		constraintDetails.relationshipOperator = RelationshipOperator.EQUALS;
		constraintDetails.attributeName = attributeName;
		constraintDetails.operativeValue = operativeValue;
		
		return constraintDetails;
	}
	
	public static Constraint or(Constraint... constraint) {
		ConstraintCollection constraintLocal = new ConstraintCollection();
		constraintLocal.logicalOperator = LogicalOperator.OR;
		
		constraintLocal.constraintArray = constraint;
		
		return constraintLocal;
	}
}
