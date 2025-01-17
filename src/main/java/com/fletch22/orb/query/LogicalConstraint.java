package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.constraint.CollectConstraintChildCriteriaVisitor;
import com.fletch22.orb.query.constraint.CollectCriteriaWithAttributeVisitor;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.ConstraintProcessVisitor;
import com.fletch22.orb.query.constraint.ConstraintRegistrationVisitor;
import com.fletch22.orb.query.constraint.ConstraintRenameChildCriteriaAttributeVisitor;
import com.fletch22.orb.query.constraint.ConstraintSetParentVisitor;
import com.fletch22.orb.query.constraint.ConstraintShaper;
import com.fletch22.orb.query.criteria.Criteria;
import com.googlecode.cqengine.query.Query;

public class LogicalConstraint extends Constraint {
	
	public LogicalConstraint() {
		super();
	}
	
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
		
		if (this.logicalOperator != null) {
			description.append(this.logicalOperator.toString());
		}
		
		for (Constraint constraint : this.constraintList) {
			description = constraint.getDescription(description);
		}
		
		return description;
	}
}
