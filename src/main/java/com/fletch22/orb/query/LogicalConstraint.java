package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.Arrays;

public class LogicalConstraint extends Constraint {
	public LogicalOperator logicalOperator;
	public ArrayList<Constraint> constraintList = new ArrayList<Constraint>();
	
	public LogicalConstraint(LogicalOperator logicalOperator, Constraint ...constraintArray) {
		this.logicalOperator = logicalOperator;
		this.constraintList.addAll(Arrays.asList(constraintArray));
	}
	
	public static LogicalConstraint and(Constraint... constraintArray) {
		return createLogicalConstraint(LogicalOperator.AND, constraintArray);
	}
	
	public static LogicalConstraint or(Constraint... constraintArray) {
		return createLogicalConstraint(LogicalOperator.OR, constraintArray);
	}
	
	private static LogicalConstraint createLogicalConstraint(LogicalOperator logicalOperator, Constraint[] constraintArray) {
		LogicalConstraint logicalConstraint = new LogicalConstraint(logicalOperator, constraintArray);
		
		return logicalConstraint;
	}

	@Override
	public Constraint[] getConstraints() {
		
		return constraintList.toArray(new Constraint[constraintList.size()]);
	}
}
