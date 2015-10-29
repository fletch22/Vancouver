package com.fletch22.orb;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder;
import com.fletch22.orb.modules.system.OrbSystemModule;

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
	
	private ArrayList<OrbSystemModule> orbSystemModuleList = new ArrayList<OrbSystemModule>();
	
	@PostConstruct
	public void postConstruct() {
		initializeSystemTypes();
	}
	
	public void addOrbSystemModule(OrbSystemModule orbSystemModule) {
		this.orbSystemModuleList.add(orbSystemModule);
	}
	
	public void clearOrbSystemModules() {
		this.orbSystemModuleList.clear();
	}
	
	private void initializeSystemTypes() {
		orbTypeManager.initializeOrbTypes();
	}
	
	public void nukeAndPaveAllIntegratedSystems() {
		transactionService.endTransaction();
		cache.nukeAllItemsFromCache();
		logActionService.clearOutDatabase();
		initializeSystem();
	}
	
	public void initializeSystem() {
		initializeSystemTypes();
		for (OrbSystemModule orbSystemModule : orbSystemModuleList) {
			orbSystemModule.initialize();
		}
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
