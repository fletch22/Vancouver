package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.RelationshipOperator;

public abstract class ConstraintDet extends Constraint {
	
	public ConstraintDet() {
		super();
	}
		
	public abstract RelationshipOperator getRelationshipOperator();
}
