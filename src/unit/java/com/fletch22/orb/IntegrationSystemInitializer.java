package com.fletch22.orb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionDao;
import com.fletch22.redis.ObjectAppCacheService;

@Component
public class IntegrationSystemInitializer {

	@Autowired
	ObjectAppCacheService objectAppCacheService;
	
	@Autowired
	LogActionDao logActionDao;
	
	public void nukeAndPaveAllIntegratedSystems() {
		objectAppCacheService.clearAllItemsFromCache();
		logActionDao.clearOutDatabase();
	}
}
