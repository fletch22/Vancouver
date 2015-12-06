package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.Arrays;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.ConstraintProcessVisitor;
import com.fletch22.orb.query.constraint.ConstraintRegistrationVisitor;
import com.fletch22.orb.query.constraint.ConstraintSetParentVisitor;
import com.googlecode.cqengine.query.Query;

public class LogicalConstraint extends Constraint {
	public LogicalOperator logicalOperator = null;
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
		return new LogicalConstraint(logicalOperator, constraintArray);
	}

	@Override
	public Constraint[] getConstraints() {
		return constraintList.toArray(new Constraint[constraintList.size()]);
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
	public void acceptConstraintSetParent(ConstraintSetParentVisitor constraintSetParentVisitor) {
		constraintSetParentVisitor.visit(this);
	}
}
