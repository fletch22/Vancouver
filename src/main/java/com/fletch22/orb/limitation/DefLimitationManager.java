package com.fletch22.orb.limitation;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.CriteriaManager;

public interface DefLimitationManager extends CriteriaManager {
	
	public CriteriaCollection getCriteriaCollection();
	
	public long addToCollectionWithPreCheckConstraint(Criteria criteria);
}

