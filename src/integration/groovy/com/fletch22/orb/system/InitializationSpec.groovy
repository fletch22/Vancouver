package com.fletch22.orb.system

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.dao.LogActionDao
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage
import com.fletch22.orb.command.transaction.TransactionService
import com.fletch22.orb.service.OrbTypeService

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
	OrbTypeService orbTypeService
	
	def cleanup() {
		this.logActionDao.resetCurrentTransaction()
	}
	

	@Test
	def 'test'() {
		
		given:
		orbTypeService.addOrbType("test")
		
		this.logActionDao.recordTransactionStart(transactionService.generateTranId());
		
		TransactionService.transactionTimeoutInSeconds = 1;
		
		Thread.sleep(2000);
		
		when:
		this.initialization.initializeSystem();
		int i = 1;
		
		then:
		i == 1
	}
	
}
