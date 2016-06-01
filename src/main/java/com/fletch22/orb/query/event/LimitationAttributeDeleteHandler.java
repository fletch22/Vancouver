package com.fletch22.orb.query.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.limitation.LimitationManager;
import com.fletch22.orb.query.criteria.CriteriaAttributeDeleteHandler;
import com.fletch22.orb.query.criteria.CriteriaManager;

@Component
public class LimitationAttributeDeleteHandler extends CriteriaAttributeDeleteHandler {

	@Autowired
	LimitationManager limitationManager;

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return limitationManager.getCriteriaCollection();
	}
	
	@Override
	protected CriteriaManager getCriteriaManager() {
		return limitationManager;
	}
}
