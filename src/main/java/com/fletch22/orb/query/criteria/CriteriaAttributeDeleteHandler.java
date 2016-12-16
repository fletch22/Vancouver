package com.fletch22.orb.query.criteria;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.LogicalConstraint;

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

	// NOTE: If an attribute is deleted then then all the criteria associated with it is deleted.
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
