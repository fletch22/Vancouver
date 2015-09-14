package com.fletch22.orb.query;

public abstract class ConstraintDetails extends Constraint {

	public abstract String getAttributeName();
	
	public abstract RelationshipOperator getRelationshipOperator();
	
}
