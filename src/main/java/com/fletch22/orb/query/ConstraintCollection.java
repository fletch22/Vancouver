package com.fletch22.orb.query;

public class ConstraintCollection extends Constraint {
	
	LogicalOperator logicalOperator;
	Constraint[] constraintArray;
	
	@Override
	public Constraint[] getConstraints() {
		return constraintArray;
	}
}
