package com.fletch22.orb.query;

public class ConstraintCollection extends Constraint {
	
	Constraint[] constraintArray;
	
	@Override
	public Constraint[] getConstraints() {
		return constraintArray;
	}
}
