package com.fletch22.orb.query.constraint;

import com.fletch22.orb.query.CriteriaAggregate;
import com.fletch22.orb.query.CriteriaCollector;
import com.fletch22.orb.query.LogicalConstraint;

public class CollectCriteriaWithAttributeVisitor {

	String attributeToFind;
	CriteriaCollector criteriaCollector;

	public CollectCriteriaWithAttributeVisitor(CriteriaCollector criteriaCollector, String attributeToFind) {
		this.attributeToFind = attributeToFind;
		this.criteriaCollector = criteriaCollector;
	}

	public boolean visit(LogicalConstraint logicalConstraint) {
		boolean result = false;
		for (Constraint constraint : logicalConstraint.constraintList) {
			result = constraint.acceptCollectCriteriaWithAttributeVisitor(this);
			if (result) {
				break;
			}
		}
		return result;
	}

	public boolean visit(ConstraintDetailsAggregate constraintDetailsAggregate) {
		boolean result = false;
		CriteriaAggregate criteriaAgg = constraintDetailsAggregate.criteriaForAggregation;

		if (doesConstraintDetailsHaveAttribute(constraintDetailsAggregate) || criteriaAgg.fieldOfInterest.equals(attributeToFind)) {
			result = true;
		}

		if (!result) {
			result = criteriaCollector.doesCriteriaHaveAttribute(criteriaAgg, this.attributeToFind);
		}

		return result;
	}

	public boolean visit(ConstraintDetailsList constraintDetailsList) {
		return doesConstraintDetailsHaveAttribute(constraintDetailsList);
	}

	public boolean visit(ConstraintDetailsSingleValue constraintDetailsSingleValue) {
		return doesConstraintDetailsHaveAttribute(constraintDetailsSingleValue);
	}

	public boolean doesConstraintDetailsHaveAttribute(ConstraintDetails constraintDetails) {
		boolean result = false;
		if (constraintDetails.attributeName.equals(this.attributeToFind)) {
			result = true;
		}
		return result;
	}
}
