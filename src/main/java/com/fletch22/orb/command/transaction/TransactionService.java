package com.fletch22.orb.command.transaction;

import java.math.BigDecimal;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionDao;
import com.fletch22.dao.LogActionDao.TransactionSearchResult;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.util.NowFactory;

@Component
public class TransactionService {
	
	Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
	public static final BigDecimal NO_TRANSACTION_IN_FLIGHT = null;
	private static int transactionTimeoutInSeconds = 10;

	@Autowired 
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	LogActionDao logActionDao;
	
	@Autowired
	NowFactory nowFactory;
	
	private BigDecimal transactionIdInFlight;
	
	public BigDecimal beginTransaction() {
		
		BigDecimal tranId = tranDateGenerator.getTranDate();
		
		return beginTransaction(tranId);
	}
	
	public BigDecimal beginTransaction(BigDecimal tranId) {
		if (isTransactionInFlight()) {
			String tranIdMessage = (NO_TRANSACTION_IN_FLIGHT == this.transactionIdInFlight) ? "(null)" : this.transactionIdInFlight.toString();
			throw new RuntimeException("Encountered problem while trying to begin a transaction. There is already a transaction underway '" + tranIdMessage + "'. The system does not yet support nested transactions.");
		}
		
		this.logActionDao.recordTransactionStart(tranId);
		
		this.transactionIdInFlight = tranId;
		
		return this.transactionIdInFlight;
	}
	
	public void endTransaction() {
		this.transactionIdInFlight = NO_TRANSACTION_IN_FLIGHT;
	}
	
	public boolean isTransactionInFlight() {
		return NO_TRANSACTION_IN_FLIGHT != this.transactionIdInFlight;
	}
	
	public BigDecimal generateTranDate() {
		return this.tranDateGenerator.getTranDate();
	}
	
	public BigDecimal generateTranId() { 
		return this.tranDateGenerator.getTranDate();
	}

	public void rollbackToBeforeSpecificTransaction(BigDecimal tranId) {
		this.logActionDao.rollbackToBeforeSpecificTransaction(tranId);
		this.transactionIdInFlight = NO_TRANSACTION_IN_FLIGHT;
	}
	
	public void rollbackCurrentTransaction() {
		this.logActionDao.rollbackCurrentTransaction();
		this.transactionIdInFlight = NO_TRANSACTION_IN_FLIGHT;
	}

	public void commitTransaction() {
		this.logActionDao.clearCurrentTransaction();
		this.transactionIdInFlight = NO_TRANSACTION_IN_FLIGHT;
	}
	
	public boolean doesDatabaseHaveAnExpiredTransaction() {
		boolean hasExpiredTransaction = false;
		BigDecimal tranId = this.logActionDao.getCurrentTransactionIfAny();
		
		DateTime tranDate = null;
		
		if (tranId != NO_TRANSACTION_IN_FLIGHT) {
			tranDate = this.tranDateGenerator.convertToNearestMillisecond(tranId);
			DateTime currentDateTime = nowFactory.getNow();
			Seconds secondsBetween = Seconds.secondsBetween(tranDate, currentDateTime);
			hasExpiredTransaction = (secondsBetween.getSeconds() > transactionTimeoutInSeconds);
		}
		
		return hasExpiredTransaction;
	}

	public BigDecimal getCurrentTransactionId() {
		return this.transactionIdInFlight;
	}
	
	public Optional<BigDecimal> getSubsequantTransaction(BigDecimal tranId) {
		TransactionSearchResult transactionSearchResult = this.logActionDao.getSubsequentTransactionIfAny(tranId);
		
		Optional<BigDecimal> optional = Optional.empty();
		if (transactionSearchResult.wasTransactionFound()) {
			optional = Optional.of(transactionSearchResult.tranId);
		}
		return optional;
	}
	
	public void rollbackToSpecificTransaction(BigDecimal tranId) {
		
		// NOTE: This technique is not pure but reduces some store proc creation work.
		TransactionSearchResult transactionSearchResult = this.logActionDao.getSubsequentTransactionIfAny(tranId);
		if (transactionSearchResult.wasTransactionFound()) {
			this.rollbackToBeforeSpecificTransaction(transactionSearchResult.tranId); 
		}
	}
}
