package com.fletch22.orb.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.CriteriaCollection;

@Component
public class QueryAttributeDeleteHandler extends CriteriaAttributeDeleteHandler {

	@Autowired
	QueryManager queryManager;

	@Autowired
	Cache cache;

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return cache.queryCollection;
	}
	
	protected QueryManager getCriteriaManager() {
		return queryManager;
	}
}
