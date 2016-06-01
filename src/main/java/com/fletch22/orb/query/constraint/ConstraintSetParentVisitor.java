package com.fletch22.orb.query.constraint;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.criteria.Criteria;

public class ConstraintSetParentVisitor {
	
	Logger logger = LoggerFactory.getLogger(ConstraintSetParentVisitor.class);
	
	Criteria criteria;
	
	public ConstraintSetParentVisitor(Criteria criteria) {
		this.criteria = criteria;
	}

	public void visit(LogicalConstraint logicalConstraint) {
		List<Constraint> list = logicalConstraint.constraintList;
		for (Constraint constraint : list) {
			constraint.acceptConstraintSetParentVisitor(this);
		}
	}
	
	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate) {
		Criteria child = constraintDetailsAggregate.criteriaForAggregation;
		child.setParentId(this.criteria.getCriteriaId());
	}
	
	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue) {
		// Do Nothing
	}
	
	public void visit(ConstraintDetailsList constraintDetailsList) {
		// Do Nothing
	}
}
