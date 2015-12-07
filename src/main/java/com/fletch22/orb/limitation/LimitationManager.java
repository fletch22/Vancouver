package com.fletch22.orb.limitation;

import java.util.List;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.CriteriaImpl;
import com.fletch22.orb.query.CriteriaManager;

public interface LimitationManager extends CriteriaManager {
	List<CriteriaImpl> getOrbsTypeCriteria(long orbTypeInternalId);
	
	public CriteriaCollection getCriteriaCollection();
}

