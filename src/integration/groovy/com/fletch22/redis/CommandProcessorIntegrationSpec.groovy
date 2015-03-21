package com.fletch22.redis;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.command.CommandBundle
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.orbType.GetListOfOrbTypesCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.OperationResult
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage
import com.fletch22.orb.command.processor.OperationResult.OpResult
import com.fletch22.orb.command.transaction.BeginTransactionCommand
import com.fletch22.orb.command.transaction.CommitTransactionCommand
import com.fletch22.orb.command.transaction.TransactionService

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CommandProcessorIntegrationSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandProcessorIntegrationSpec)
	
	@Autowired
	CommandProcessor commandProcessor
	
	@Autowired
	ObjectTypeCacheService objectTypeCacheService
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	@Autowired
	GetListOfOrbTypesCommand getListOfOrbTypesCommand
	
	@Autowired
	BeginTransactionCommand beginTransactionCommand
	
	@Autowired
	CommitTransactionCommand commitTransactionCommand
	
	@Autowired
	TransactionService transactionService
	
	def setup() {
		initializer.nukeAndPaveAllIntegratedSystems();
	}
	
	def tearDown() {
		initializer.nukeAndPaveAllIntegratedSystems();
	}

	@Test
	def 'test command processor integration with backend'() {
		
		given:
		def typeLabel = 'foo'
		
		CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(addOrbTypeCommand.toJson(typeLabel))
		
		when:
		commandProcessor.executeAction(commandProcessActionPackage)
		
		then:
		commandProcessActionPackage.undoActionBundle
		commandProcessActionPackage.undoActionBundle.getActions().size() == 1
	}
	
	@Test
	def 'test command bundle add 2 commands happy path'() {
		
		given:
		setup()
		
		def orbTypeName1 = 'foo1'
		def orbTypeName2 = 'foo2'
		
		CommandBundle commandBundle = new CommandBundle();
		def jsonAddOrbTypeCommand = this.addOrbTypeCommand.toJson(orbTypeName1);
		commandBundle.addCommand(jsonAddOrbTypeCommand);

		def jsonAddOrbTypeCommand2 = this.addOrbTypeCommand.toJson(orbTypeName2);
		commandBundle.addCommand(jsonAddOrbTypeCommand2);
		
		assertEquals('Should be equal.', commandBundle.getActionList().size(), 2);
				
		def commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(commandBundle.toJson());
		
		when:
		this.commandProcessor.processAction(commandProcessActionPackage)
		
		then:
		commandProcessActionPackage.undoActionBundle
		commandProcessActionPackage.undoActionBundle.getActions().size() == 2
	}
	
	@Test
	def 'test command bundle add 2 commands bad command'() {
		
		given:
		setup()
		
		def orbTypeName1 = 'foo1'
		
		assertTrue(objectTypeCacheService.getTypes().size() == 0)
		
		CommandBundle commandBundle = new CommandBundle()
		def jsonAddOrbTypeCommand = this.addOrbTypeCommand.toJson(orbTypeName1)
		commandBundle.addCommand(jsonAddOrbTypeCommand)
		commandBundle.addCommand(new StringBuilder('{\"AllOfYourPieces\": \"Ridonculous command\"}'))
		
		def commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
		
		when: 
		this.commandProcessor.processAction(commandProcessActionPackage)
		
		then:
		commandProcessActionPackage.undoActionBundle
		commandProcessActionPackage.undoActionBundle.getActions().size() == 0
		objectTypeCacheService.getTypes().size() == 0
	}
	
	@Test
	def 'test begin, commit transaction integration with backend'() {
		
		given: 'The beginning of a transaction'
		
		def json = this.beginTransactionCommand.toJson()
		CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(json)
		OperationResult operationResult = commandProcessor.executeAction(commandProcessActionPackage)
		
		assert operationResult.opResult == OpResult.SUCCESS
		
		BigDecimal tranId = (BigDecimal) operationResult.operationResultObject
						
		and: 'an orb type is added'
		def typeLabel = 'foo'
		json = this.addOrbTypeCommand.toJson(typeLabel)
		commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(json)
		operationResult = commandProcessor.executeAction(commandProcessActionPackage)
		
		assert operationResult.opResult == OpResult.SUCCESS
		
		when: 'the transaction is committed'
		json = this.commitTransactionCommand.toJson(tranId)
		commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(json)
		operationResult = commandProcessor.executeAction(commandProcessActionPackage)
		
		if (operationResult.operationResultException != null) {
			logger.info("Exception message: {}", operationResult.operationResultException.printStackTrace())
		}
		
		then: 'the transaction persists'
		operationResult.opResult == OpResult.SUCCESS
		
		and: 'there is no longer a "current" transaction '
		!this.transactionService.isTransactionInFlight()
	}
}
