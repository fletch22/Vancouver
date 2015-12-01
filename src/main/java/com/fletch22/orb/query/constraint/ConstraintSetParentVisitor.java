package com.fletch22.orb.query.constraint;

import java.util.List;

import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.LogicalConstraint;

public class ConstraintSetParentVisitor {
	
	Criteria criteria;
	
	public ConstraintSetParentVisitor(Criteria criteria) {
		this.criteria = criteria;
	}

	public void visit(LogicalConstraint logicalConstraint) {
		List<Constraint> list = logicalConstraint.constraintList;
		for (Constraint constraint : list) {
			constraint.acceptConstraintSetParent(this);
		}
	}
	
	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate) {
		Criteria child = constraintDetailsAggregate.criteriaForAggregation;
		child.setParent(this.criteria);
		ConstraintSetParentVisitor constraintSetParentVisitor = new ConstraintSetParentVisitor(child);
		child.logicalConstraint.acceptConstraintSetParent(constraintSetParentVisitor);
	}
	
	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue) {
		// Do Nothing
	}
	
	public void visit(ConstraintDetailsList constraintDetailsList) {
		// Do Nothing
	}
}
