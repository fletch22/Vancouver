package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.constraint.CollectCriteriaWithAttributeVisitor;

@Component
public class CriteriaCollector {

	public List<Criteria> collectCriteria(CriteriaCollection criteriaCollection, long orbTypeInternalId, String attributeToFind) {
		
		List<Criteria> criteriaNeedingChange = new ArrayList<Criteria>();
		
		Set<Long> criteriaIdSet = collectCriteriaKeysWithAttribute(criteriaCollection, orbTypeInternalId, attributeToFind);
		for (long key: criteriaIdSet) {
			criteriaNeedingChange.add(criteriaCollection.getByQueryId(key));
		}
		
		return criteriaNeedingChange;
	}
	
	public Set<Long> collectCriteriaKeysWithAttribute(CriteriaCollection criteriaCollection, long orbTypeInternalId, String attributeToFind) {
		
		Set<Long> criteriaNeedingChange = new HashSet<Long>();
		
		Set<Long> criteriaKey = criteriaCollection.getKeys();
		for (long id : criteriaKey) {
			Criteria criteria = criteriaCollection.getByQueryId(id);

			if (criteria.getOrbTypeInternalId() == orbTypeInternalId) {
				
				if (doesCriteriaHaveAttribute(criteria, attributeToFind)) {
					criteriaNeedingChange.add(criteria.getCriteriaId());
				}
			}
		}
		
		return criteriaNeedingChange;
	}
	
	public boolean doesCriteriaHaveAttribute(Criteria criteria, String attributeToFind) {
		boolean result = false;
		
		if (criteria.hasConstraints()) {
			CollectCriteriaWithAttributeVisitor visitor = new CollectCriteriaWithAttributeVisitor(this, attributeToFind);
			result = criteria.logicalConstraint.acceptCollectCriteriaWithAttributeVisitor(visitor);
		}
		
		return result;
	}

}
