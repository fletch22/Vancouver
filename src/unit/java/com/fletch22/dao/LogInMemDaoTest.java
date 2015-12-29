package com.fletch22.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import com.fletch22.dao.Dao.ActionInfo;
import com.fletch22.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class LogInMemDaoTest {
	
	Logger logger = LoggerFactory.getLogger(LogInMemDaoTest.class);
	
	@Autowired
	LogActionInMemDao logActionInMemDao;
	
	@Autowired
	LogActionDaoImpl logActionDaoImpl;
	
	@Before
	public void before() {
		logActionInMemDao.clearOutDatabase();
	}
	
	@Test
	public void testConnection() {
		
		int count = 10;

		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();
		for (int i = 0; i < count; i++) {
			logActionInMemDao.getConnection();
			logActionInMemDao.closeConnection();
		}
		stopWatch.stop();
		
		stopWatch.logElapsed();
		
		stopWatch.reset();
		
		stopWatch.start();
		for (int i = 0; i < count; i++) {
			logActionDaoImpl.getConnection();
			logActionDaoImpl.closeConnection();
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
			logActionInMemDao.clearOutDatabase();
		}
		stopWatch.stop();
		
		stopWatch.logElapsed("clear out in mem");
		
		stopWatch.reset();
		
		stopWatch.start();
		for (int i = 0; i < count; i++) {
			logActionDaoImpl.clearOutDatabase();
		}
		stopWatch.stop();
		
		stopWatch.logElapsed("clear out in impl");
	}
	
	@Test
	public void testLogAction() {
		
		BigDecimal tranDate = new BigDecimal("2134.0001");
		BigDecimal tranId = tranDate;
		
		StringBuilder action = new StringBuilder();
		action.append("This is the action to log.");
		
		StringBuilder undoAction = new StringBuilder();
		undoAction.append("This is the undo action to log.");
		
		logActionInMemDao.logAction(action, undoAction, tranId, tranDate);
		
	}
	
	@Test
	public void testGetUndosForTransactionAndSubesequentTransactions() {
		
		BigDecimal tranId = new BigDecimal("123123213.0000001");
		logActionInMemDao.getUndosForTransactionAndSubesequentTransactions(tranId);
	}
	
	@Test 
	public void testgetUndosForTransactionAndSubesequentTransactions() {
		
		BigDecimal tranId = new BigDecimal("23452543.23452543");
		logActionInMemDao.getUndosForTransactionAndSubesequentTransactions(tranId);
	}
	
	@Test 
	public void testGetCountCommand() {
		
		int count = logActionInMemDao.countCommands();
		
		assertEquals(0, count);
	}
	
	@Test
	public void testGetActionsAll() {
		
		List<ActionInfo> actionInfos = logActionInMemDao.getAllActions();
		
		assertNotNull(actionInfos);
	}
	
	@Test
	public void testRecordTransactionStart() {
		
		BigDecimal currentTranId = logActionInMemDao.getCurrentTransactionIfAny();
		
		assertEquals(Dao.NO_TRANSACTION_FOUND, currentTranId);
		
		BigDecimal tranId = new BigDecimal("123213.892890890132");
		
		logActionInMemDao.recordTransactionStart(tranId);
		
		currentTranId = logActionInMemDao.getCurrentTransactionIfAny();
		
		assertEquals(tranId, currentTranId);
	}
	
	@Test
	public void testRollbackToBeforeSpecificTransaction() {
		
		BigDecimal tranId = new BigDecimal("123213.892890890132");
		
		logActionInMemDao.recordTransactionStart(tranId);
		
		BigDecimal currentTranId = logActionInMemDao.getCurrentTransactionIfAny();
		
		assertEquals(tranId, currentTranId);
		
		logActionInMemDao.rollbackToBeforeSpecificTransaction(tranId);
		
		currentTranId = logActionInMemDao.getCurrentTransactionIfAny();
		
		assertEquals(Dao.NO_TRANSACTION_FOUND, currentTranId);
	}
	
	
}
