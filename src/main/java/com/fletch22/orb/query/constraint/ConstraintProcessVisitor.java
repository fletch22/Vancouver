package com.fletch22.orb.query.constraint;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.LogicalConstraint;
import com.googlecode.cqengine.query.Query;

public interface ConstraintProcessVisitor {

	public Query<CacheEntry> visit(LogicalConstraint logicalConstraint, ConstraintShaper constraintShaper);
	public Query<CacheEntry> visit(ConstraintDetailsList constraintDetailsList, ConstraintShaper constraintShaper);
	public Query<CacheEntry> visit(ConstraintDetailsSingleValue constraintDetailsSingleValue, ConstraintShaper constraintShaper);
	public Query<CacheEntry> visit(ConstraintDetailsAggregate constraintDetailsAggregate, ConstraintShaper constraintShaper);
}
