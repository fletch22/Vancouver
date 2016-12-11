package com.fletch22.orb.query.constraint;

public abstract class ConstraintDetails extends ConstraintDet {
	
	public ConstraintDetails() {
		super();
	}
	
	public String attributeName;

	public abstract String getAttributeName();
}
