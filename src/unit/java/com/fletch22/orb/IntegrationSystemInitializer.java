package com.fletch22.orb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionDao;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.command.transaction.TransactionService;

@Component
public class IntegrationSystemInitializer {

	@Autowired
	Cache cache;
	
	@Autowired
	LogActionDao logActionDao;
	
	@Autowired
	TransactionService transactionService;
	
	public void nukeAndPaveAllIntegratedSystems() {
		transactionService.endTransaction();
		cache.clearAllItemsFromCache();
		logActionDao.clearOutDatabase();
	}
}
