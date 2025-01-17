package com.fletch22.orb.query.constraint;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.criteria.Criteria;
import com.googlecode.cqengine.query.Query;

public class ConstraintDetailsSingleValue extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public String operativeValue;
	
	public ConstraintDetailsSingleValue() {
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

	public String getOperativeValue() {
		return this.operativeValue;
	}
	
	@Override
	public Query<CacheEntry> acceptConstraintProcessorVisitor(ConstraintProcessVisitor constraintVisitor, ConstraintShaper constraintShaper) {
		return constraintVisitor.visit(this, constraintShaper);
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
		
		description.append(this.attributeName);
		description.append(StringUtils.SPACE);
		description.append(this.relationshipOperator.toString());
		description.append(StringUtils.SPACE);
		description.append(this.operativeValue);
		
		return description;
	}
}
