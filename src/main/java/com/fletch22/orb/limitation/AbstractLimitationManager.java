package com.fletch22.orb.limitation;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.AbstractCriteriaManager;

public abstract class AbstractLimitationManager extends AbstractCriteriaManager {
	
	@Autowired
	LimitationCollection limitationCollection;

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return limitationCollection;
	}
}
