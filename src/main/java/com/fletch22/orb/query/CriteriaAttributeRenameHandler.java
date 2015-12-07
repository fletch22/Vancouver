package com.fletch22.orb.query;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.CriteriaImpl;
import com.fletch22.orb.query.constraint.ConstraintDetails;
import com.fletch22.orb.query.constraint.ConstraintRenameChildCriteriaAttributeVisitor;
import com.fletch22.orb.query.sort.CriteriaSortInfo;

@Component
public class CriteriaAttributeRenameHandler {

	public void handleAttributeRename(CriteriaCollection criteriaCollection, long orbTypeInternalId, String attributeOldName, String attributeNewName) {

		Set<Long> criteriaKey = criteriaCollection.getKeys();
		for (long id : criteriaKey) {
			CriteriaImpl criteria = criteriaCollection.getByQueryId(id);

			if (criteria.getOrbType().id == orbTypeInternalId) {
				renameInConstraints(criteria, attributeOldName, attributeNewName);
				renameInSortInfo(criteria, attributeOldName, attributeNewName);
			}
		}
	}

	private void renameInSortInfo(CriteriaImpl criteria, String attributeOldName, String attributeNewName) {

		List<CriteriaSortInfo> criteriaSortInfoList = criteria.getSortInfoList();
		for (CriteriaSortInfo criteriaSortInfo : criteriaSortInfoList) {

			if (criteriaSortInfo.sortAttributeName.equals(attributeOldName)) {
				criteriaSortInfo.sortAttributeName = attributeNewName;
			}
		}
	}

	private void renameInConstraints(CriteriaImpl criteria, String attributeOldName, String attributeNewName) {
		if (criteria.hasConstraints()) {
			
			ConstraintRenameChildCriteriaAttributeVisitor visitor = new ConstraintRenameChildCriteriaAttributeVisitor(attributeOldName, attributeNewName);
			criteria.logicalConstraint.acceptConstraintRenameChildCriteriaAttributeVisitor(visitor);
		}
	}
}
