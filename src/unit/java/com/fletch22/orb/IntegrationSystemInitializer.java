package com.fletch22.orb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionDao;
import com.fletch22.orb.cache.local.CacheService;

@Component
public class IntegrationSystemInitializer {

	@Autowired
	CacheService cacheService;
	
	@Autowired
	LogActionDao logActionDao;
	
	public void nukeAndPaveAllIntegratedSystems() {
		cacheService.clearAllItemsFromCache();
		logActionDao.clearOutDatabase();
	}
}
