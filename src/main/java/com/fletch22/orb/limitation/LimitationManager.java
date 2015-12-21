package com.fletch22.orb.limitation;

import java.util.Map;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.CriteriaManager;

public interface LimitationManager extends CriteriaManager {
	
	Map<Long, Criteria> getOrbsTypeCriteria(long orbTypeInternalId);
	
	public CriteriaCollection getCriteriaCollection();
}

