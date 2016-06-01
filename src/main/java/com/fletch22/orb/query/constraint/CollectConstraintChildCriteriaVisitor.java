package com.fletch22.orb.query.constraint;

import java.util.List;

import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaManager;

public class CollectConstraintChildCriteriaVisitor {
	
	CriteriaManager criteriaManager;
	
	public CollectConstraintChildCriteriaVisitor(CriteriaManager criteriaManager) {
		this.criteriaManager = criteriaManager;
	}
	
	public void visit(LogicalConstraint logicalConstraint, List<Criteria> criteriaList) {
		for (Constraint constraint : logicalConstraint.constraintList) {
			constraint.acceptCollectConstraintChildCriteriaVisitor(this, criteriaList);
		}
	}

	public void visit(ConstraintDetailsAggregate constraintDetailsAggregate, List<Criteria> criteriaList) {
		
		Criteria criteriaAgg = constraintDetailsAggregate.criteriaForAggregation;

		criteriaList.add(criteriaAgg);
		
		criteriaManager.collectCriteriaChildren(criteriaAgg, criteriaList);
	}

	public void visit(ConstraintDetailsList constraintDetailsList, List<Criteria> criteriaList) {
		// Do Nothing
	}

	public void visit(ConstraintDetailsSingleValue constraintDetailsSingleValue, List<Criteria> criteriaList) {
		// Do Nothing
	}
}
