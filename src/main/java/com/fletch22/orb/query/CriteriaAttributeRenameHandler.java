package com.fletch22.orb.query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.constraint.ConstraintRenameChildCriteriaAttributeVisitor;
import com.fletch22.orb.query.sort.CriteriaSortInfo;

@Component
public class CriteriaAttributeRenameHandler {
	
	@Autowired
	CriteriaCloner criteriaCloner;
	
	@Autowired
	CriteriaCollector criteriaCollector;
	
	Logger logger = LoggerFactory.getLogger(CriteriaAttributeRenameHandler.class);
	
	public void handleAttributeRename(CriteriaManager criteriaManger, long orbTypeInternalId, String attributeOldName, String attributeNewName) {

		List<Criteria> criteriaNeedingChange = criteriaCollector.collectCriteria(criteriaManger.getCriteriaCollection(), orbTypeInternalId, attributeOldName);
		
		for (Criteria criteria: criteriaNeedingChange) {
			renameFieldInCriteria(criteriaManger, criteria, orbTypeInternalId, attributeOldName, attributeNewName);
		}
	}

	public void renameFieldInCriteria(CriteriaManager criteriaManger, Criteria criteria, long orbTypeInternalId, String attributeOldName, String attributeNewName) {
		renameInConstraints(criteria, attributeOldName, attributeNewName);
		renameInSortInfo(criteria, attributeOldName, attributeNewName);
	}

	private void renameInSortInfo(Criteria criteria, String attributeOldName, String attributeNewName) {

		List<CriteriaSortInfo> criteriaSortInfoList = criteria.getSortInfoList();
		for (CriteriaSortInfo criteriaSortInfo : criteriaSortInfoList) {

			if (criteriaSortInfo.sortAttributeName.equals(attributeOldName)) {
				criteriaSortInfo.sortAttributeName = attributeNewName;
			}
		}
	}

	private void renameInConstraints(Criteria criteria, String attributeOldName, String attributeNewName) {
		if (criteria.hasConstraints()) {
			ConstraintRenameChildCriteriaAttributeVisitor visitor = new ConstraintRenameChildCriteriaAttributeVisitor(attributeOldName, attributeNewName);
			criteria.logicalConstraint.acceptConstraintRenameChildCriteriaAttributeVisitor(visitor);
		}
	}
}
