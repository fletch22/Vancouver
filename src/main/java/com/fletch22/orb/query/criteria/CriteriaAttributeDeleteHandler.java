package com.fletch22.orb.query.criteria;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.ConstraintDetails;
import com.fletch22.orb.query.constraint.ConstraintDetailsList;
import com.fletch22.orb.query.constraint.ConstraintDetailsSingleValue;

public abstract class CriteriaAttributeDeleteHandler {

	public abstract CriteriaCollection getCriteriaCollection();

	protected abstract CriteriaManager getCriteriaManager();

	@Autowired
	public CriteriaCollector criteriaCollector;

	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		CriteriaCollection criteriaCollection = getCriteriaCollection();

		List<Criteria> criteriaNeedingChange = criteriaCollector.collectCriteria(criteriaCollection, orbTypeInternalId, attributeName);

		for (Criteria criteria : criteriaNeedingChange) {
			handleAttributeDeletion(criteria.logicalConstraint, criteria.getCriteriaId(), attributeName, isDeleteDependencies);
		}
	}

	protected void handleAttributeDeletion(LogicalConstraint logicalConstraint, long queryOrbInternalId, String attributeName, boolean isDeleteDependencies) {
		if (isDeleteDependencies) {
			if (getCriteriaManager().doesCriteriaExist(queryOrbInternalId)) {
				getCriteriaManager().delete(queryOrbInternalId, isDeleteDependencies);
			}
		} else {
			throw new RuntimeException("The attribute has a dependent query. System will not allow deletion.");
		}
	}
}
