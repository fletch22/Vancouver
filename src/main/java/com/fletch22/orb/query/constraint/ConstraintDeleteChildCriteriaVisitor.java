package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.CriteriaImpl;
import com.fletch22.orb.query.CriteriaManager;
import com.fletch22.orb.query.LogicalConstraint;

public class ConstraintDeleteChildCriteriaVisitor {
	
	CriteriaManager criteriaManager;
	boolean isDeleteDependencies;
	
	public ConstraintDeleteChildCriteriaVisitor(CriteriaManager criteriaManager, boolean isDeleteDependencies) {
		this.criteriaManager = criteriaManager;
		this.isDeleteDependencies = isDeleteDependencies;
	}
	
	public void visit(LogicalConstraint logicalConstraint) {
		for (Constraint constraint : logicalConstraint.constraintList) {
			constraint.acceptConstraintDeleteChildCriteriaVisitor(this);
		}
	}

	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate) {
		
		CriteriaImpl criteriaAgg = constraintDetailsAggregate.criteriaForAggregation;
		
		criteriaManager.delete(criteriaAgg.getCriteriaId(), isDeleteDependencies);
	}

	public void visit(ConstraintDetailsList constraintDetailsList) {
		// Do Nothing
	}

	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue) {
		// Do Nothing
	}
	
}
