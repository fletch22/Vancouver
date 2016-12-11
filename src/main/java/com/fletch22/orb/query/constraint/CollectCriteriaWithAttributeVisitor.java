package com.fletch22.orb.query.constraint;

import java.util.Arrays;

import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.criteria.CriteriaAggregate;
import com.fletch22.orb.query.criteria.CriteriaCollector;

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

		if (doesConstraintDetailsAggregateHaveAttribute(constraintDetailsAggregate) || Arrays.asList(criteriaAgg.fieldOfInterest).contains(attributeToFind)) {
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
	
	public boolean doesConstraintDetailsAggregateHaveAttribute(ConstraintDetailsAggregate constraintDetailsAggregate) {
		boolean result = false;
		if (Arrays.asList(constraintDetailsAggregate.attributeNames).contains(this.attributeToFind)) {
			result = true;
		}
		return result;
	}

	public boolean doesConstraintDetailsHaveAttribute(ConstraintDetails constraintDetails) {
		boolean result = false;
		if (constraintDetails.attributeName.equals(this.attributeToFind)) {
			result = true;
		}
		return result;
	}
}
