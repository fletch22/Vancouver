package com.fletch22.orb.query.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaManager;

public class ConstraintRegistrationVisitor {
	
	static Logger logger = LoggerFactory.getLogger(ConstraintRegistrationVisitor.class);
	
	CriteriaManager criteriaManager;
	
	public ConstraintRegistrationVisitor(CriteriaManager criteriaManager) {
		this.criteriaManager = criteriaManager;
	}
	
	public void visit(LogicalConstraint logicalConstraint) {
		for (Constraint constraintChild : logicalConstraint.getConstraints()) {
			constraintChild.acceptConstraintRegistrationVisitor(this);
		}
	}
	
	public void visit(ConstraintDetailsList constraintDetailsList) {
		// Do nothing
	}
	
	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue) {
		// Do nothing
	}
	
	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate) {
		
		Criteria child = constraintDetailsAggregate.criteriaForAggregation;
		this.criteriaManager.addToCollection(child);
		
		LogicalConstraint logicalConstraint = child.logicalConstraint;
		if (logicalConstraint != null) {
			visit(logicalConstraint);
		}
	}
}
