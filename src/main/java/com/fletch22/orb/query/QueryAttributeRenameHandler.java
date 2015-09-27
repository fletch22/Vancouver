package com.fletch22.orb.query;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.sort.CriteriaSortInfo;

@Component
public class QueryAttributeRenameHandler {
	
	@Autowired
	Cache cache;

	public void handleAttributeRename(long orbTypeInternalId, String attributeOldName, String attributeNewName) {
		QueryCollection queryCollection = cache.queryCollection;
		
		Set<Long> criteriaKey = queryCollection.getKeys();
		for (long key : criteriaKey) {
			Criteria criteria = queryCollection.get(key);
			
			renameInConstraints(criteria, attributeOldName, attributeNewName);
			renameInSortInfo(criteria, attributeOldName, attributeNewName);
		}
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
		List<LogicalConstraint> logicalConstraintList = criteria.logicalConstraintList;
		for (LogicalConstraint logicalConstraint : logicalConstraintList) {
			handleAttributeRename(logicalConstraint, attributeOldName, attributeNewName);
		}
	}
	
	private void handleAttributeRename(LogicalConstraint logicalConstraint, String attributeOldName, String attributeNewName) {
		Constraint[] constraintArray = logicalConstraint.constraint.getConstraints();
		
		for (Constraint constraintInner: constraintArray) {
			
			if (constraintInner instanceof ConstraintDetailsSingleValue) {
				handleAttributeRename((ConstraintDetails) constraintInner, attributeOldName, attributeNewName);
			} else if (constraintInner instanceof ConstraintDetailsList) {
				handleAttributeRename((ConstraintDetails) constraintInner, attributeOldName, attributeNewName);
			} else if (constraintInner instanceof ConstraintCollection) {
				LogicalConstraint logicalConstraintLocal = new LogicalConstraint(logicalConstraint.logicalOperator, constraintInner);
				handleAttributeRename(logicalConstraintLocal, attributeOldName, attributeNewName);
			} else if (constraintInner instanceof LogicalConstraint) {
				handleAttributeRename((LogicalConstraint) constraintInner, attributeOldName, attributeNewName);
			}
		}
	}

	private void handleAttributeRename(ConstraintDetails constraintDetails, String attributeOldName, String attributeNewName) {
		if (constraintDetails.attributeName.equals(attributeOldName)) {
			constraintDetails.attributeName = attributeNewName;
		}
	}

}
