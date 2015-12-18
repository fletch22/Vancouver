package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.RelationshipOperator;

public abstract class ConstraintDetails extends Constraint {
	
	public ConstraintDetails() {
		super();
	}
	
	public String attributeName;

	public abstract String getAttributeName();
	
	public abstract RelationshipOperator getRelationshipOperator();
}
