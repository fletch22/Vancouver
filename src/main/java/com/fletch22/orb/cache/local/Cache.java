package com.fletch22.orb.cache.local;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.QueryManager;

@Component
public class Cache {

	public OrbCollection orbCollection;
	public OrbTypeCollection orbTypeCollection;
	public QueryCollection queryCollection;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired 
	QueryManager queryManager;
	
	@Autowired
	CacheComponentsFactory cacheComponentsFactory;
	
	@PostConstruct
	public void postConstruct() {
		CacheComponentsDto dto = cacheComponentsFactory.getInstance();
		orbTypeCollection = dto.orbTypeCollection;
		orbCollection = dto.orbCollection;
		queryCollection = dto.queryCollection;
	}
	
	public CacheComponentsDto getCacheComponentsDto() {
		return cacheComponentsFactory.getInstance(this);
	}

	public void nukeAllItemsFromCache() {
		 orbManager.nukeAllOrbs();
		 orbTypeManager.nukeAllTypes();
		 queryManager.nukeAllQueries();
	}
}
