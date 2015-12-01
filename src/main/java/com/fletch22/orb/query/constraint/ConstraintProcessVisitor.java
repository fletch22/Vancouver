package com.fletch22.orb.query.constraint;

import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.LogicalConstraint;
import com.googlecode.cqengine.query.Query;

public interface ConstraintProcessVisitor {

	public Query<CacheEntry> visit(LogicalConstraint logicalConstraint, long orbTypeInternalId);
	public Query<CacheEntry> visit(ConstraintDetailsList constraintDetailsList, long orbTypeInternalId);
	public Query<CacheEntry> visit(ConstraintDetailsSingleValue constraintDetailsSingleValue, long orbTypeInternalId);
	public Query<CacheEntry> visit(ConstraintDetailsAggregate constraintDetailsAggregate, long orbTypeInternalId);
}
