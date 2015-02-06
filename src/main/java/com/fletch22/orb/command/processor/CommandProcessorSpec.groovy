package com.fletch22.orb.command.processor;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

import com.fletch22.command.ActionSniffer
import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.InternalIdGenerator
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.processor.OperationResult.OpResult

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class CommandProcessorSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandProcessorSpec.class);

	CommandProcessor commandProcessor

	OrbTypeManager orbTypeManager

	@Autowired
	AddOrbTypeCommand addOrbTypeCommand

	@Autowired
	ActionSniffer actionSniffer
	
	@Autowired
	InternalIdGenerator internalIdGenerator

	def setup() {
		this.orbTypeManager = Mock(OrbTypeManager)
		this.commandProcessor = new CommandProcessor()
		this.commandProcessor.orbTypeManager = this.orbTypeManager
		this.commandProcessor.actionSniffer = this.actionSniffer
		this.commandProcessor.addOrbTypeCommand = this.addOrbTypeCommand
		this.commandProcessor.internalIdGenerator = this.internalIdGenerator
		
		this.orbTypeManager.createOrbType(*_) >> 333.longValue()
		this.actionSniffer.getVerb(*_) >> CommandExpressor.ADD_ORB_TYPE
	}

	@Test
	def 'test addOrbTypeCommand success'() {
		
		given:
		setup()
		
		assertNotNull(this.commandProcessor)
		
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage()
		commandProcessActionPackage.action = this.addOrbTypeCommand.toJson('foo')
		commandProcessActionPackage.tranDate = new BigDecimal(123)
		commandProcessActionPackage.operationResult = OperationResult.IN_THE_MIDDLE

		when:
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		then:
		operationResult
		operationResult.opResult == OpResult.SUCCESS 
		operationResult.operationResultException == null
		operationResult.rollbackAction.is(commandProcessActionPackage.operationResult.rollbackAction)
		operationResult.tranDate.is(commandProcessActionPackage.tranDate)
		
	}
}
