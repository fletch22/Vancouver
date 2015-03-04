package com.fletch22.redis;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.command.CommandBundle
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.orbType.GetListOfOrbTypesCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
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
}
