package com.fletch22.orb.query;

import java.util.List;

public class ConstraintDetailsList extends ConstraintDetails {
	
	RelationshipOperator relationshipOperator;
	String attributeName;
	List<String> operativeValueList;
	
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
}
