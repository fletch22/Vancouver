package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.LogicalConstraint;

public class ConstraintRenameChildCriteriaAttributeVisitor {
	
	String oldAttributeName;
	String newAttributeName;
	
	public ConstraintRenameChildCriteriaAttributeVisitor(String oldAttributeName, String newAttributeName) {
		this.oldAttributeName = oldAttributeName;
		this.newAttributeName = newAttributeName;
	}
	
	public void visit(LogicalConstraint logicalConstraint) {
		for (Constraint constraint : logicalConstraint.constraintList) {
			constraint.acceptConstraintRenameChildCriteriaAttributeVisitor(this);
		}
	}

	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate) {
		renameAttribute(constraintDetailsAggregate);
		
		Criteria criteriaAgg = constraintDetailsAggregate.criteriaForAggregation;
		
		if (criteriaAgg.hasConstraints()) {
			criteriaAgg.logicalConstraint.acceptConstraintRenameChildCriteriaAttributeVisitor(this);
		}
	}
	
	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue) {
		renameAttribute(constraintDetailsSingleValue);
	}
	
	public void visit(ConstraintDetailsList constraintDetailsList) {
		renameAttribute(constraintDetailsList);
	}

	public void renameAttribute(ConstraintDetails constraintDetails) {
		if (constraintDetails.attributeName.equals(oldAttributeName)) {
			constraintDetails.attributeName = newAttributeName;
		}
	}

}
