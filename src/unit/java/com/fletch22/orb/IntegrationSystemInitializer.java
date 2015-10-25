package com.fletch22.orb;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder;

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
	
	@Autowired
	EventLogCommandProcessPackageHolder eventLogCommandProcessPackageHolder;
	
	@PostConstruct
	public void postConstruct() {
		initializeSystemTypes();
	}
	
	private void initializeSystemTypes() {
		orbTypeManager.initializeOrbTypes();
	}
	
	public void nukeAndPaveAllIntegratedSystems() {
		transactionService.endTransaction();
		cache.nukeAllItemsFromCache();
		logActionService.clearOutDatabase();
		initializeSystemTypes();
	}
	
	public void initializeSystem() {
		transactionService.endTransaction();
		cache.nukeAllItemsFromCache();
		initializeSystemTypes();
		logActionService.loadCacheFromDb();
	}
	
	public void verifyClean() {
		
		if (transactionService.isTransactionInFlight()) {
			throw new RuntimeException("Transaction is still in flight. Ensure cleanup.");
		}

		if (eventLogCommandProcessPackageHolder.hasInitialCommandActionBeenAdded() ) {
			throw new RuntimeException("Unprocessed command in " + eventLogCommandProcessPackageHolder.getClass().getSimpleName() + ". Ensure cleanup.");
		}
	}
}
