package com.fletch22.orb.query.constraint;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.CriteriaAggregate;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;
import com.googlecode.cqengine.query.Query;

public class ConstraintDetailsAggregate extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public Aggregate aggregate;
	public CriteriaAggregate criteriaForAggregation;
	
	@Override
	public Constraint[] getConstraints() {
		
		Constraint[] constraintArray = new Constraint[1];
		
		constraintArray[0] = this;
		
		return constraintArray;
	}

	@Override
	public String getAttributeName() {
		return this.attributeName;
	}
	
	@Override
	public RelationshipOperator getRelationshipOperator() {
		return this.relationshipOperator;
	}

	@Override
	public Query<CacheEntry> acceptConstraintProcessorVisitor(ConstraintProcessVisitor constraintVisitor, long orbTypeInternalId) {
		return constraintVisitor.visit(this, orbTypeInternalId);
	}
	
	@Override
	public void acceptConstraintRegistrationVisitor(ConstraintRegistrationVisitor constraintRegistrationVisitor) {
		constraintRegistrationVisitor.visit(this);
	}

	@Override
	public void acceptConstraintSetParentVisitor(ConstraintSetParentVisitor constraintSetParentVisitor) {
		constraintSetParentVisitor.visit(this);
	}
	
	@Override
	public void acceptConstraintDeleteChildCriteriaVisitor(ConstraintDeleteChildCriteriaVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptConstraintRenameChildCriteriaAttributeVisitor(ConstraintRenameChildCriteriaAttributeVisitor visitor) {
		visitor.visit(this);
	}
}
