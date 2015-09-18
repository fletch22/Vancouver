package com.fletch22.orb;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.command.transaction.TransactionService;

@Component
public class IntegrationSystemInitializer {

	@Autowired
	Cache cache;
	
	@Autowired
	LogActionService logActionService;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@PostConstruct
	public void postConstruct() {
		initializeSystemTypes();
	}
	
	private void initializeSystemTypes() {
		orbTypeManager.initializeOrbTypes();
	}
	
	public void nukeAndPaveAllIntegratedSystems() {
		transactionService.endTransaction();
		cache.clearAllItemsFromCache();
		logActionService.clearOutDatabase();
		initializeSystemTypes();
	}
	
	public void initializeSystem() {
		transactionService.endTransaction();
		cache.clearAllItemsFromCache();
		logActionService.loadCacheFromDb();
	}
}
