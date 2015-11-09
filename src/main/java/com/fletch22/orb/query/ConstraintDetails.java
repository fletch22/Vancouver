package com.fletch22.orb.query;

public abstract class ConstraintDetails extends Constraint {
	
	public String attributeName;

	public abstract String getAttributeName();
	
	public abstract RelationshipOperator getRelationshipOperator();
}
