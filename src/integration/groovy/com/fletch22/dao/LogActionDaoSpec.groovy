package com.fletch22.dao;

import static org.junit.Assert.*

import java.sql.Connection

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.dao.LogActionDao.TransactionSearchResult
import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.TranDateGenerator
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand
import com.fletch22.orb.command.transaction.TransactionService

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class LogActionDaoSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(LogActionDaoSpec)
	
	@Autowired
	LogActionDao logActionDao;
	
	@Autowired
	TranDateGenerator tranDateGenerator
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	CommandExpressor commandExpressor
	
	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand
	
	@Autowired
	TransactionService transactionService
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
	def 'test is connection openable'() {
		
		given:
		
		when:
		Connection connection = logActionDao.getConnection()
		
		then:
		logActionDao.isConnectionOpen()
	} 
	
	@Test
	def 'test is connection closed'() {
		
		given:
		
		when:
		Connection connection = logActionDao.getConnection()
		
		connection.close()
		
		then:
		connection
		false == logActionDao.isConnectionOpen()
	}
	
	@Test
	def 'clear out database'() {
		
		given:
				
		when:
		logActionDao.clearOutDatabase();
		
		then:
		int numberCommands = logActionDao.countCommands()
		numberCommands == 0
	}
	
	@Test
	def 'testConnection'() {
		
		given:
		when:
		String connectionString = logActionDao.getConnectionString()
		
		then:
		connectionString
	}
	
	@Test
	def 'insert sample record into database'() {
		
		given:
		def action = this.addOrbTypeCommand.toJson("foo");
		def undoAction = this.deleteOrbTypeCommand.toJson(1234, false);
		
		def tranDate = tranDateGenerator.getTranDate();
		
		tranDate = tranDate.add(new BigDecimal(".000000000001"));
		def tranId = tranDate;
		
		logger.debug("TD: {}", tranDate);
		
		when:
		logActionDao.logAction(action, undoAction, tranDate, tranId);
		
		then:
		1 == 1
	}
	
	@Test
	def 'test subsequent tran'() {
		given:
		BigDecimal tranIdFirst = transactionService.beginTransaction()
		
		this.orbTypeManager.createOrbType("foo", null)
		
		transactionService.commitTransaction();
		
		TransactionSearchResult transactionSearchResult = logActionDao.getSubsequentTransactionIfAny(tranIdFirst)
		
		tranIdFirst = transactionSearchResult.tranId
		
		BigDecimal tranIdSecond = transactionService.beginTransaction()
		
		this.orbTypeManager.createOrbType("bar", null)
		
		transactionSearchResult = logActionDao.getSubsequentTransactionIfAny(tranIdFirst)
		
		def tranId2 = transactionSearchResult.tranId;
		
		transactionService.commitTransaction()
		
		when:
		transactionSearchResult = logActionDao.getSubsequentTransactionIfAny(tranIdFirst)
		
		then:
		transactionSearchResult.tranId == tranId2
	}
}
