package com.fletch22.orb.limitation;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.local.OrbCollection;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.AbstractCriteriaManager;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.OrbResultSet;

public abstract class AbstractLimitationManager extends AbstractCriteriaManager {
	
	@Autowired
	LimitationCollection limitationCollection;
	
	@Autowired
	private Cache cache;

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return limitationCollection;
	}

	public long addToCollectionWithPreCheckConstraint(Criteria criteria) {

		OrbCollection orbCollection = cache.orbCollection;
		long countTotal = orbCollection.getCountOrbsOfType(criteria.getOrbTypeInternalId());
		if (countTotal > 0) {
			OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria);
			int countMatches = orbResultSet.orbList.size();
			if (countTotal != countMatches) {
				String message = String.format("Constraint violation. Total recs: %s; Matches: %s", countTotal, countMatches);
				throw new RuntimeException(message);
			}
		}
		
		return addToCollection(criteria);
	}
}
