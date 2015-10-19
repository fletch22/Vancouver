package com.fletch22.orb.client.service;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage
import com.fletch22.orb.command.transaction.RollbackTransactionService;

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class RollbackServiceSpec extends Specification {

	@Autowired
	RollbackTransactionService rollbackService
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	CommandProcessor commandProcessor
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	@Autowired
	BeginTransactionService beginTransactionService
	
	def setup() {
		this.initializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def cleanup() {
		this.initializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
	def 'test rollback'() {
		given:
		def tranId = beginTransactionService.beginTransaction()
		
		when:
		this.rollbackService.rollbackToSpecificTransaction(tranId)
		
		then:
		notThrown(Exception)
	}
	
	private CommandProcessActionPackage insertTypes(Integer numberOfAdds) {
		BigDecimal tranId;
		for (i in 1..numberOfAdds.intValue()) {
			orbTypeManager.createOrbType("foo_" + i, null)
		}
	}
}
