package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.criteria.CriteriaAggregate;

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
		
		CriteriaAggregate criteriaAgg = constraintDetailsAggregate.criteriaForAggregation;
		renameFieldOfInterest(criteriaAgg);
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
	
	public void renameAttribute(ConstraintDetailsAggregate constraintDetailsAggregate) {
		for (int i = 0 ; i < constraintDetailsAggregate.attributeNames.length; i++) {
			if (constraintDetailsAggregate.attributeNames[i].equals(oldAttributeName)) {
				constraintDetailsAggregate.attributeNames[i] = newAttributeName;
			}
		}
	}
	
	public void renameFieldOfInterest(CriteriaAggregate criteriaAggregate) {
		for (int i = 0 ; i < criteriaAggregate.fieldOfInterest.length; i++) {
			if (criteriaAggregate.fieldOfInterest[i].equals(oldAttributeName)) {
				criteriaAggregate.fieldOfInterest[i] = newAttributeName;
			}
		}
	}
}
