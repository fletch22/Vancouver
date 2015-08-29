package com.fletch22.orb.client.service;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.IntegrationTests;
import com.fletch22.orb.client.service.RollbackTransactionService;
import com.fletch22.orb.command.CommandBundle
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.OperationResult
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class RollbackServiceSpec extends Specification {

	@Autowired
	RollbackTransactionService rollbackService
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	CommandProcessor commandProcessor
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	def setup() {
		this.initializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def cleanup() {
		this.initializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
	def 'test rollback'() {
		given:
		def commandProcessActionPackage = insertTypes(5)
		
		when:
		this.rollbackService.rollbackToSpecificTransaction(commandProcessActionPackage.tranId)
		
		then:
		notThrown(Exception)
	}
	
	private CommandProcessActionPackage insertTypes(Integer numberOfAdds) {
		def commandProcessActionPackage = null
		BigDecimal tranId;
		for (i in 1..numberOfAdds.intValue()) {
			def json = addOrbTypeCommand.toJson('foo' + i)
			
			CommandBundle commandBundle = new CommandBundle()
			
			commandBundle.addCommand(json);
			commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
			if (null != tranId) {
				commandProcessActionPackage.setTranId(tranId)
			}
			
			OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
			
			if (null == tranId) {
				tranId = commandProcessActionPackage.getTranId()
			}
		}
		
		return commandProcessActionPackage
	}
}
