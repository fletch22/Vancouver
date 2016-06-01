package com.fletch22.orb.query.constraint;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.constraint.aggregate.Aggregate;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaAggregate;
import com.googlecode.cqengine.query.Query;

public class ConstraintDetailsAggregate extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public Aggregate aggregate;
	public CriteriaAggregate criteriaForAggregation;
	
	public ConstraintDetailsAggregate() {
		super();
	}
	
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
	public void acceptConstraintRenameChildCriteriaAttributeVisitor(ConstraintRenameChildCriteriaAttributeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptCollectConstraintChildCriteriaVisitor(CollectConstraintChildCriteriaVisitor visitor, List<Criteria> criteriaList) {
		visitor.visit(this, criteriaList);
	}

	@Override
	public boolean acceptCollectCriteriaWithAttributeVisitor(CollectCriteriaWithAttributeVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public StringBuffer getDescription(StringBuffer description) {
		
		description.append(StringUtils.SPACE);
		description.append("the value for attribute '");
		description.append(this.attributeName);
		description.append("' ");
		description.append(this.relationshipOperator.toString());
		description.append(StringUtils.SPACE);
		description.append(this.aggregate.toString());
		description.append(" for");
		description.append(this.criteriaForAggregation.getDescription());
		
		return description;
	}
}
