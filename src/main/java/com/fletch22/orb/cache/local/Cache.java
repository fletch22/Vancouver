package com.fletch22.orb.cache.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.orb.limitation.LimitationManager;
import com.fletch22.orb.query.QueryManager;

@Component
public class Cache {
	
	private static final Logger logger = LoggerFactory.getLogger(Cache.class);

	@Autowired
	public OrbCollection orbCollection;
	
	@Autowired
	public OrbTypeCollection orbTypeCollection;
	
	@Autowired
	public QueryCollection queryCollection;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired 
	QueryManager queryManager;
	
	@Autowired
	LimitationManager limitationManager;
	
	@Autowired
	DefLimitationManager defLimitationManager;
	
	@Autowired
	CacheComponentsFactory cacheComponentsFactory;
	
	public CacheComponentsDto getCacheComponentsDto() {
		return cacheComponentsFactory.getInstance(this);
	}

	public void nukeAllItemsFromCache() {
		orbManager.nukeAndPave();
		orbTypeManager.nukeAndPave();
		queryManager.nukeAndPave();
		limitationManager.nukeAndPave();
		defLimitationManager.nukeAndPave();
	}
}
