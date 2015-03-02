package com.fletch22.orb.command.processor;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.dao.LogActionService
import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.InternalIdGenerator
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.command.ActionSniffer
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
import com.fletch22.orb.command.processor.OperationResult.OpResult
import com.fletch22.orb.transaction.UndoService

class CommandProcessorSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandProcessorSpec);

	@Shared CommandProcessor commandProcessor

	def setup() {
		this.commandProcessor = new CommandProcessor()

		LogActionService logActionService = Mock()
		this.commandProcessor.logActionService = logActionService

		ActionSniffer actionSniffer = Mock()		
		this.commandProcessor.actionSniffer = actionSniffer
		
		actionSniffer.getVerb(*_) >> CommandExpressor.ADD_ORB_TYPE
		
		InternalIdGenerator internalIdGenerator = Mock()
		this.commandProcessor.internalIdGenerator = internalIdGenerator
		internalIdGenerator.getCurrentId() >> 1001
		
		OrbTypeManager orbTypeManager = Mock()
		this.commandProcessor.orbTypeManager = orbTypeManager
		
		orbTypeManager.createOrbType(*_) >> 333.longValue()

		AddOrbTypeCommand addOrbTypeCommand = Mock()
		this.commandProcessor.addOrbTypeCommand = addOrbTypeCommand
		
		AddOrbTypeDto addOrbTypeDto = Mock()
		addOrbTypeCommand.fromJson(*_) >> addOrbTypeDto
		
		UndoService rollbackService = Mock()
		this.commandProcessor.rollbackService = rollbackService
	}

	@Test
	def 'test process Action'() {
		
		given:
		def commandProcessActionPackage = CommandProcessActionPackageMother.getGoodOne()
		commandProcessActionPackage.isInRestoreMode = false

		when:
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		logger.info("Size: {}", commandProcessActionPackage.undoActionBundle.getActions().size());
		
		then:
		operationResult
		operationResult.opResult == OpResult.SUCCESS 
		operationResult.operationResultException == null
		operationResult.internalIdAfterOperation != operationResult.internalIdBeforeOperation
		commandProcessActionPackage.undoActionBundle != null
	}
	
	@Test
	def 'handleLoggingAndRollbackTest'() {
		
		given:
		def commandProcessActionPackage = CommandProcessActionPackageMother.getGoodOne()
		commandProcessActionPackage.undoActionBundle.getActions().push(new StringBuilder("{}"));
		
		OperationResult operationResult = new OperationResult(opResult)
		operationResult.shouldBeLogged = shouldBeLogged
		
		commandProcessActionPackage.isInRestoreMode = isInRestoreMode
		
		when:
		this.commandProcessor.handleLoggingAndRollback(commandProcessActionPackage, operationResult)
		
		then:
		notThrown(Exception)
		this.commandProcessor
		commandProcessActionPackage.undoActionBundle.getActions().size() == numberOfUndoActionsRemaining
		
		where:
		shouldBeLogged 	| isInRestoreMode	| opResult			| numberOfUndoActionsRemaining
		true			| false				| OpResult.SUCCESS	| 1
		false			| false				| OpResult.FAILURE	| 1
	}
}
