package com.fletch22.dao;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackage
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.OperationResult
import com.fletch22.orb.rollback.UndoActionBundle

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class LogActionServiceSpec extends Specification {
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	@Autowired
	LogActionService logActionService
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	CommandProcessor commandProcessor
	
	def setup() {
		initializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
	def 'test get result set'() {
		
		given:
		setup()
		
		def json = addOrbTypeCommand.toJson('foo')
		
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		commandProcessActionPackage.action = json
		
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		when:
		UndoActionBundle undoActionBundle = logActionService.getUndoActions(123)
		
		then:
		undoActionBundle
		undoActionBundle.getActions().size() > 0
	}
}
