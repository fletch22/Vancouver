package com.fletch22.orb.query;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class QueryAttributeDeleteHandler {

	@Autowired
	QueryManager queryManager;

	@Autowired
	Cache cache;

	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		QueryCollection queryCollection = cache.queryCollection;

		Set<Long> criteriaKey = queryCollection.getKeys();
		for (long key : criteriaKey) {

			Criteria criteria = queryCollection.get(key);
			if (criteria.getOrbType().id == orbTypeInternalId) {

				boolean hasQueryBeenDeleted = false;
				List<LogicalConstraint> logicalConstraintList = criteria.logicalConstraintList;
				for (LogicalConstraint logicalConstraint : logicalConstraintList) {
					hasQueryBeenDeleted = handleAttributeDeletion(logicalConstraint, key, attributeName, isDeleteDependencies);
					if (hasQueryBeenDeleted) {
						break;
					}
				}
			}
		}
	}

	private boolean handleAttributeDeletion(LogicalConstraint logicalConstraint, long orbInternalIdQuery, String attributeName, boolean isDeleteDependencies) {
		boolean hasBeenDeleted = false;

		boolean hasAttribute = hasAttribute(logicalConstraint, attributeName);
		if (hasAttribute) {
			if (isDeleteDependencies) {
				queryManager.delete(orbInternalIdQuery, isDeleteDependencies);

				hasBeenDeleted = true;
			} else {
				throw new RuntimeException("The attribute has a dependent query. System will not allow deletion.");
			}
		}

		return hasBeenDeleted;
	}

	private boolean hasAttribute(LogicalConstraint logicalConstraint, String attributeName) {
		Constraint[] constraintArray = logicalConstraint.constraint.getConstraints();
		boolean hasAttribute = false;

		for (Constraint constraintInner : constraintArray) {

			if (constraintInner instanceof ConstraintDetailsSingleValue) {
				hasAttribute = hasAttribute((ConstraintDetails) constraintInner, attributeName);
			} else if (constraintInner instanceof ConstraintDetailsList) {
				hasAttribute = hasAttribute((ConstraintDetails) constraintInner, attributeName);
			} else if (constraintInner instanceof ConstraintCollection) {
				LogicalConstraint logicalConstraintLocal = new LogicalConstraint(logicalConstraint.logicalOperator, constraintInner);
				hasAttribute = hasAttribute(logicalConstraintLocal, attributeName);
			} else if (constraintInner instanceof LogicalConstraint) {
				hasAttribute = hasAttribute((LogicalConstraint) constraintInner, attributeName);
			}

			if (hasAttribute) {
				break;
			}
		}

		return hasAttribute;
	}

	private boolean hasAttribute(ConstraintDetails constraintDetails, String attributeName) {
		boolean hasAttribute = false;
		if (constraintDetails.attributeName.equals(attributeName)) {
			hasAttribute = true;
		}

		return hasAttribute;
	}
}
