package com.fletch22.orb.query;

import java.util.List;
import java.util.Set;

import org.mockito.internal.stubbing.answers.DoesNothing;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public abstract class CriteriaAttributeDeleteHandler {

	public abstract CriteriaCollection getCriteriaCollection();
	
	protected abstract QueryManager getCriteriaManager();
	
	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		CriteriaCollection criteriaCollection = getCriteriaCollection();

		Set<Long> criteriaKey = criteriaCollection.getKeys();
		for (long key : criteriaKey) {

			Criteria criteria = criteriaCollection.getByQueryId(key);
			if (criteria.getOrbType().id == orbTypeInternalId) {
				handleAttributeDeletion(criteria.logicalConstraint, key, attributeName, isDeleteDependencies);
			}
		}
	}
	
	protected boolean hasAttribute(ConstraintDetails constraintDetails, String attributeName) {
		boolean hasAttribute = false;
		if (constraintDetails.attributeName.equals(attributeName)) {
			hasAttribute = true;
		}

		return hasAttribute;
	}
	
	protected boolean hasAttribute(LogicalConstraint logicalConstraint, String attributeName) {
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
	
	protected boolean handleAttributeDeletion(LogicalConstraint logicalConstraint, long queryOrbInternalId, String attributeName, boolean isDeleteDependencies) {
		boolean hasBeenDeleted = false;

		boolean hasAttribute = hasAttribute(logicalConstraint, attributeName);
		if (hasAttribute) {
			if (isDeleteDependencies) {
				if (getCriteriaManager().doesCriteriaExist(queryOrbInternalId)) {
					getCriteriaManager().delete(queryOrbInternalId, isDeleteDependencies);
				}
				hasBeenDeleted = true;
			} else {
				throw new RuntimeException("The attribute has a dependent query. System will not allow deletion.");
			}
		}

		return hasBeenDeleted;
	}
}
