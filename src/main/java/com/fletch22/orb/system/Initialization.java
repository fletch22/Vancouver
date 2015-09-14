package com.fletch22.orb.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.transaction.TransactionService;

@Component
public class Initialization {
	
	Logger logger = LoggerFactory.getLogger(Initialization.class);

	@Autowired
	LogActionService logActionService;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	Cache cache;
	
	public OperationResult initializeSystem() {
		OperationResult operationResult = OperationResult.getInstanceFailure();
		
		nukePaveAndRepopulate();
		if (transactionService.doesDatabaseHaveAnExpiredTransaction()) {
			logger.info("Database has an expired transaction. Will rollback and repopulate cache.");
			transactionService.rollbackCurrentTransaction();
		} else {
			operationResult = OperationResult.getInstanceSuccess();
		}
		
		return operationResult;
	}

	private void nukePaveAndRepopulate() {
		
		cache.clearAllItemsFromCache();
		transactionService.endTransaction();
		
		logActionService.loadCacheFromDb();
	}
}
