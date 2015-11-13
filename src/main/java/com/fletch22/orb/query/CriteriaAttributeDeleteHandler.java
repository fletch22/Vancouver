package com.fletch22.orb.query;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class CriteriaAttributeDeleteHandler {

	@Autowired
	QueryManager queryManager;

	@Autowired
	Cache cache;

	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		QueryCollection queryCollection = cache.queryCollection;

		Set<Long> criteriaKey = queryCollection.getKeys();
		for (long key : criteriaKey) {

			Criteria criteria = queryCollection.getByQueryId(key);
			if (criteria.getOrbType().id == orbTypeInternalId) {
				handleAttributeDeletion(criteria.logicalConstraint, key, attributeName, isDeleteDependencies);
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
		List<Constraint> constraintList = logicalConstraint.constraintList;
		boolean hasAttribute = false;

		for (Constraint constraintInner : constraintList) {

			if (constraintInner instanceof ConstraintDetailsSingleValue) {
				hasAttribute = hasAttribute((ConstraintDetails) constraintInner, attributeName);
			} else if (constraintInner instanceof ConstraintDetailsList) {
				hasAttribute = hasAttribute((ConstraintDetails) constraintInner, attributeName);
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
