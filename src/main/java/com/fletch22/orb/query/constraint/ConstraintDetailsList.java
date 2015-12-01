package com.fletch22.orb.query.constraint;

import java.util.List;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.RelationshipOperator;
import com.googlecode.cqengine.query.Query;

public class ConstraintDetailsList extends ConstraintDetails {
	
	public RelationshipOperator relationshipOperator;
	public List<String> operativeValueList;
	
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
	public Query<CacheEntry> acceptConstraintProcessorVisitor(ConstraintProcessVisitor constraintVisitor, long orbTypeInternalId) {
		return constraintVisitor.visit(this, orbTypeInternalId);
	}
}
