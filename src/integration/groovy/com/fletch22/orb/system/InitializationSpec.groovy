package com.fletch22.orb.system

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.dao.LogActionDao
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.client.service.OrbTypeService;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.transaction.TransactionService

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class InitializationSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(InitializationSpec)
	
	@Autowired
	Initialization initialization
	
	@Autowired
	LogActionDao logActionDao
	
	@Autowired
	TransactionService transactionService
	
	@Autowired
	CommandProcessor commandProcessor
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	OrbTypeService orbTypeService
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def cleanup() {
		this.logActionDao.resetCurrentTransaction()
	}
	
	@Test
	def 'test'() {
		
		given:
		orbTypeService.addOrbType("test")
		
		BigDecimal bd = this.logActionDao.getCurrentTransactionIfAny()
		
		logger.info("BD: {}", bd.toString())
		
		this.logActionDao.recordTransactionStart(transactionService.generateTranId())
		
		TransactionService.transactionTimeoutInSeconds = 1
		
		Thread.sleep(2000)
		
		when:
		this.initialization.initializeSystem()
		
		then:
		BigDecimal currentTransaction = this.logActionDao.getCurrentTransactionIfAny()
		assert currentTransaction.compareTo(this.logActionDao.NO_TRANSACTION_FOUND) == 0
	}
	
}
