package com.fletch22.orb.query.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.orb.query.CriteriaAttributeDeleteHandler;
import com.fletch22.orb.query.CriteriaManager;

@Component
public class DefLimitationAttributeDeleteHandler extends CriteriaAttributeDeleteHandler {

	@Autowired
	DefLimitationManager defLimitationManager;

	@Autowired
	Cache cache;
	
	@Autowired

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return defLimitationManager.getCriteriaCollection();
	}
	
	@Override
	protected CriteriaManager getCriteriaManager() {
		return defLimitationManager;
	}
}
