package com.fletch22.orb.query.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.CriteriaAttributeDeleteHandler;
import com.fletch22.orb.query.CriteriaManager;
import com.fletch22.orb.query.QueryManager;

@Component
public class QueryAttributeDeleteHandler extends CriteriaAttributeDeleteHandler {

	@Autowired
	QueryManager queryManager;

	@Autowired
	Cache cache;

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return queryManager.getCriteriaCollection();
	}
	
	@Override
	protected CriteriaManager getCriteriaManager() {
		return queryManager;
	}
}
