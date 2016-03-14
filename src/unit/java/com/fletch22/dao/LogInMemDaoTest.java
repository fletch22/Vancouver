package com.fletch22.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.dao.LogActionDao.ActionInfo;
import com.fletch22.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class LogInMemDaoTest {
	
	Logger logger = LoggerFactory.getLogger(LogInMemDaoTest.class);
	
	@Autowired
	LogActionDao logActionDao;
	
	@Before
	public void before() {
		
		logActionDao.clearOutDatabase();
		logActionDao.clearCurrentTransaction();
	}
	
	@Test
	public void testConnection() {
		
		int count = 10;

		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();
		for (int i = 0; i < count; i++) {
			logActionDao.getConnection();
			logActionDao.closeConnection();
		}
		stopWatch.stop();
		
		stopWatch.logElapsed();
	}
	
	@Test
	public void testClearOutDatabase() {
		
		StopWatch stopWatch = new StopWatch();
		
		int count = 10;
		
		stopWatch.start();
		for (int i = 0; i < count; i++) {
			logActionDao.clearOutDatabase();
		}
		stopWatch.stop();
		
		stopWatch.logElapsed("clear out in mem");
	}
	
	@Test
	public void testLogAction() {
		
		BigDecimal tranDate = new BigDecimal("2134.0001");
		BigDecimal tranId = tranDate;
		
		StringBuilder action = new StringBuilder();
		action.append("This is the action to log.");
		
		StringBuilder undoAction = new StringBuilder();
		undoAction.append("This is the undo action to log.");
		
		logActionDao.logAction(action, undoAction, tranId, tranDate);
		
	}
	
	@Test
	public void testGetUndosForTransactionAndSubesequentTransactions() {
		
		BigDecimal tranId = new BigDecimal("123123213.0000001");
		logActionDao.getUndosForTransactionAndSubesequentTransactions(tranId);
	}
	
	@Test 
	public void testgetUndosForTransactionAndSubesequentTransactions() {
		
		BigDecimal tranId = new BigDecimal("23452543.23452543");
		logActionDao.getUndosForTransactionAndSubesequentTransactions(tranId);
	}
	
	@Test 
	public void testGetCountCommand() {
		
		int count = logActionDao.countCommands();
		
		assertEquals(0, count);
	}
	
	@Test
	public void testGetActionsAll() {
		
		List<ActionInfo> actionInfos = logActionDao.getAllActions();
		
		assertNotNull(actionInfos);
	}
	
	@Test
	public void testRecordTransactionStart() {
		
		BigDecimal currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(LogActionDao.NO_TRANSACTION_FOUND, currentTranId);
		
		BigDecimal tranId = new BigDecimal("123213.892890890132");
		
		logActionDao.recordTransactionStart(tranId);
		
		currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(tranId, currentTranId);
	}
	
	@Test
	public void testTryRecordTransactionStartTwice() {
		
		testRecordTransactionStart();
		
		BigDecimal tranId = new BigDecimal("23342342324.2544253");
		
		boolean wasExceptionThrown = false;
		
		try {
			logActionDao.recordTransactionStart(tranId);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		assertTrue(wasExceptionThrown);
	}
	
	@Test
	public void testRollbackToBeforeSpecificTransaction() {
		
		BigDecimal currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(LogActionDao.NO_TRANSACTION_FOUND, currentTranId);
		
		BigDecimal tranId = new BigDecimal("123213.892890890132");
		
		logActionDao.recordTransactionStart(tranId);
		
		currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(tranId, currentTranId);
		
		logActionDao.rollbackToBeforeSpecificTransaction(tranId);
		
		currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(LogActionDao.NO_TRANSACTION_FOUND, currentTranId);
	}
	
	@Test
	public void testResetCurrentTransaction() {
		
		BigDecimal tranId = new BigDecimal("123213.892890890132");
		
		logActionDao.recordTransactionStart(tranId);
		
		BigDecimal currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(tranId, currentTranId);
		
		logActionDao.clearCurrentTransaction();
		
		currentTranId = logActionDao.getCurrentTransactionIfAny();
		
		assertEquals(LogActionDao.NO_TRANSACTION_FOUND, currentTranId);
	}
}
