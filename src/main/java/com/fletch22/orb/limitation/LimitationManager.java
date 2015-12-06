package com.fletch22.orb.limitation;

import java.util.List;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.CriteriaManager;

public interface LimitationManager extends CriteriaManager {
	List<Criteria> getOrbsTypeCriteria(long orbTypeInternalId);
	
	public CriteriaCollection getCriteriaCollection();
}

