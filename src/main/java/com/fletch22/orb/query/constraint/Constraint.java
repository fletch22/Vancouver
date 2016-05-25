package com.fletch22.orb.query.constraint;

import java.util.List;
import java.util.UUID;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.CriteriaAggregate;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;
import com.googlecode.cqengine.query.Query;

public abstract class Constraint {
	
	public abstract Constraint[] getConstraints();
	public UUID uuid;
		
	public Constraint() {
		this.uuid = UUID.randomUUID();
	}
	
	public static Constraint eq(String attributeName, String operativeValue) {
		ConstraintDetailsSingleValue constraintDetails = new ConstraintDetailsSingleValue();
		
		constraintDetails.relationshipOperator = RelationshipOperator.EQUALS;
		constraintDetails.attributeName = attributeName;
		constraintDetails.operativeValue = operativeValue;
		
		return constraintDetails;
	}
	
	public static Constraint in(String attributeName, List<String> operativeList) {
		Constraint constraint = null;
				
		if (operativeList.size() == 1) {
			constraint = Constraint.eq(attributeName, operativeList.get(0));
		} else {
			ConstraintDetailsList constraintDetailsList = new ConstraintDetailsList();
			constraintDetailsList.relationshipOperator = RelationshipOperator.IN;
			constraintDetailsList.attributeName = attributeName;
			constraintDetailsList.operativeValueList = operativeList;
			constraint = constraintDetailsList;
		}
		
		return constraint;
	}
	
	public static Constraint is(String attributeName, Aggregate aggregate, CriteriaAggregate criteriaForAggregation) {
		ConstraintDetailsAggregate constraintDetailsAggregate = new ConstraintDetailsAggregate();
		
		constraintDetailsAggregate.relationshipOperator = RelationshipOperator.IS;
		constraintDetailsAggregate.attributeName = attributeName;
		constraintDetailsAggregate.aggregate = aggregate;
		constraintDetailsAggregate.criteriaForAggregation = criteriaForAggregation;
		
		return constraintDetailsAggregate;
	}
	
	public abstract Query<CacheEntry> acceptConstraintProcessorVisitor(ConstraintProcessVisitor visitor, long orbTypeInternalId);
	
	public abstract void acceptConstraintRegistrationVisitor(ConstraintRegistrationVisitor constraintVisitor);
	
	public abstract void acceptConstraintSetParentVisitor(ConstraintSetParentVisitor constraintSetParentVisitor); 
	
	public abstract void acceptConstraintRenameChildCriteriaAttributeVisitor(ConstraintRenameChildCriteriaAttributeVisitor visitor);
	
	public abstract void acceptCollectConstraintChildCriteriaVisitor(CollectConstraintChildCriteriaVisitor collectConstraintChildCriteriaVisitor, List<Criteria> criteriaList);
	
	public abstract boolean acceptCollectCriteriaWithAttributeVisitor(CollectCriteriaWithAttributeVisitor collectCriteriaWithAttributeVisitor);
}

