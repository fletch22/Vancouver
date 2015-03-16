package com.fletch22.orb.command.transaction;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.TranDateGenerator;

@Component
public class TransactionService {
	
	private static final BigDecimal NO_TRANSACTION_IN_FLIGHT = null;

	@Autowired 
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	LogActionService logActionService;

	private BigDecimal transactionIdInFlight;
	
	public BigDecimal beginTransaction(BigDecimal tranDate) {
		if (isTransactionInFlight()) {
			String tranId = (NO_TRANSACTION_IN_FLIGHT == this.transactionIdInFlight) ? "(null)" : this.transactionIdInFlight.toString();
			throw new RuntimeException("Encountered problem while trying to begin a transaction. There is already a transaction underway '" + tranId + "'. The system does not yet support nested transactions.");
		}
		
		this.transactionIdInFlight = getTranId();
		
		this.logActionService.beginTransaction(tranDate);
		
		return this.transactionIdInFlight;
	}
	
	public void endTransaction() {
		this.transactionIdInFlight = NO_TRANSACTION_IN_FLIGHT;
	}
	
	public boolean isTransactionInFlight() {
		return NO_TRANSACTION_IN_FLIGHT != this.transactionIdInFlight;
	}
	
	public BigDecimal getTranId() {
		BigDecimal tranId;
		if (isTransactionInFlight()) {
			tranId = transactionIdInFlight;
		} else {
			tranId = tranDateGenerator.getTranDate();
		}
		return tranId;
	}
}
