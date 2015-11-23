package com.fletch22.orb.query.constraint;

public interface ConstraintVisitor {

	public void visit(ConstraintDetailsList constraintDetailsList);
	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue);
	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate);
}
