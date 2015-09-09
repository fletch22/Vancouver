package com.fletch22.orb.query;

public class LogicalConstraint {
	public LogicalOperator logicalOperator;
	public Constraint constraint;
	
	public LogicalConstraint(LogicalOperator logicalOperator, Constraint constraint) {
		this.logicalOperator = logicalOperator;
		this.constraint = constraint;
	}
	
	public static LogicalConstraint and(Constraint... constraintArray) {
		return createLogicalConstraint(LogicalOperator.AND, constraintArray);
	}
	
	public static LogicalConstraint or(Constraint... constraintArray) {
		return createLogicalConstraint(LogicalOperator.OR, constraintArray);
	}
	
	private static LogicalConstraint createLogicalConstraint(LogicalOperator logicalOperator, Constraint[] constraintArray) {
		ConstraintCollection constraintCollection = new ConstraintCollection();
		constraintCollection.constraintArray = constraintArray;

		LogicalConstraint logicalConstraint = new LogicalConstraint(logicalOperator, constraintCollection);
		
		return logicalConstraint;
	}
}
