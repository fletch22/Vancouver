package com.fletch22.orb.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.command.transaction.TransactionService;

@Component
public class Initialization {

	@Autowired
	LogActionService logActionService;
	
	@Autowired
	TransactionService transactionService;
	
	public void initializeSystem() {
		
		if (transactionService.doesDatabaseHaveAnExpiredTransaction()) {
		}
		
	}
}
