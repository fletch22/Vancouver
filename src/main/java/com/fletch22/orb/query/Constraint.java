package com.fletch22.orb.query;

import java.util.List;

public abstract class Constraint {
	public abstract Constraint[] getConstraints();
	
	public Constraint() {}
	
	public static Constraint eq(String attributeName, String operativeValue) {
		ConstraintDetailsSingleValue constraintDetails = new ConstraintDetailsSingleValue();
		
		constraintDetails.relationshipOperator = RelationshipOperator.EQUALS;
		constraintDetails.attributeName = attributeName;
		constraintDetails.operativeValue = operativeValue;
		
		return constraintDetails;
	}
	
	public static Constraint in(String attributeName, List<String> operativeList) {
		ConstraintDetailsList constraintDetailsList = new ConstraintDetailsList();
		
		constraintDetailsList.relationshipOperator = RelationshipOperator.IN;
		constraintDetailsList.attributeName = attributeName;
		constraintDetailsList.operativeValueList = operativeList;
		
		return constraintDetailsList;
	}
}
