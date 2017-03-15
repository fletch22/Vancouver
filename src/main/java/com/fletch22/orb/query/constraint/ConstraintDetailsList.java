package com.fletch22.orb.query.constraint;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.criteria.Criteria;
import com.googlecode.cqengine.query.Query;

public class ConstraintDetailsList extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public List<String> operativeValueList;
	
	public ConstraintDetailsList() {
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
		return attributeName;
	}

	@Override
	public RelationshipOperator getRelationshipOperator() {
		return relationshipOperator;
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
		for (String value: this.operativeValueList) {
			description.append(value);
			description.append(StringUtils.SPACE);
		}
		
		return description;
	}
}
