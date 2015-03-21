package com.fletch22.orb.command.processor;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import com.fletch22.dao.LogActionService
import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.InternalIdGenerator
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.command.ActionSniffer
import com.fletch22.orb.command.orbType.AddBaseOrbTypeCommand
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage
import com.fletch22.orb.command.processor.OperationResult.OpResult
import com.fletch22.orb.command.transaction.BeginTransactionCommand
import com.fletch22.orb.command.transaction.CommitTransactionCommand;
import com.fletch22.orb.command.transaction.CommitTransactionDto;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.transaction.UndoService

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CommandProcessorSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandProcessorSpec)

	@Shared CommandProcessor commandProcessor
	
	@Shared ActionSniffer actionSniffer
	
	@Autowired
	CommandProcessActionPackageMother commandProcessActionPackageMother

	def setup() {
		this.commandProcessor = new CommandProcessor()

		LogActionService logActionService = Mock()
		this.commandProcessor.logActionService = logActionService

		this.actionSniffer = Mock(ActionSniffer)		
		this.commandProcessor.actionSniffer = actionSniffer
		
		BeginTransactionCommand beginTransactionCommand = Mock()
		this.commandProcessor.beginTransactionCommand = beginTransactionCommand
		
		TransactionService transactionService = Mock()
		this.commandProcessor.transactionService = transactionService
		
		AddOrbTypeCommand addOrbTypeCommand = Mock()
		this.commandProcessor.addOrbTypeCommand = addOrbTypeCommand
		
		AddBaseOrbTypeCommand addBaseOrbTypeCommand = Mock()
		this.commandProcessor.addOrbBaseTypeCommand = addBaseOrbTypeCommand
		
		DeleteOrbTypeCommand deleteOrbTypeCommand = Mock()
		this.commandProcessor.deleteOrbTypeCommand = deleteOrbTypeCommand
		
		InternalIdGenerator internalIdGenerator = Mock()
		this.commandProcessor.internalIdGenerator = internalIdGenerator
		internalIdGenerator.getCurrentId() >> 1001
		
		OrbTypeManager orbTypeManager = Mock()
		this.commandProcessor.orbTypeManager = orbTypeManager
		
		CommitTransactionCommand commitTransactionCommand = Mock()
		//this.commandProcessor.commitTransactionCommand
		
		orbTypeManager.createOrbType(*_) >> 333.longValue()

		AddOrbTypeDto addOrbTypeDto = Mock()
		addOrbTypeCommand.fromJson(*_) >> addOrbTypeDto
		
		UndoService rollbackService = Mock()
		this.commandProcessor.rollbackService = rollbackService
	}

	@Unroll
	@Test
	def 'test process Action'() {
		
		given:
		this.actionSniffer.getVerb(*_) >> actionVerb
		
		def commandProcessActionPackage = this.commandProcessActionPackageMother.getGoodOne(commandTypeClazz)
		commandProcessActionPackage.isInRestoreMode = false
		
		when:
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		then:
		operationResult
		operationResult.opResult == OpResult.SUCCESS 
		operationResult.operationResultException == null
		operationResult.internalIdAfterOperation != operationResult.internalIdBeforeOperation
		commandProcessActionPackage.undoActionBundle != null
		
		where:
		commandTypeClazz 				| actionVerb
		BeginTransactionCommand.class 	| CommandExpressor.BEGIN_TRANSACTION
		AddOrbTypeCommand.class			| CommandExpressor.ADD_ORB_TYPE
		DeleteOrbTypeCommand.class		| CommandExpressor.REMOVE_ORB_TYPE
		//AddBaseOrbTypeCommand.class		| OrbTypeManager.ORBTYPE_BASETYPE_ID
	}
	
	@Test
	def 'handleLoggingAndRollbackTest'() {
		
		given:
		CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageMother.getGoodOne()
		commandProcessActionPackage.undoActionBundle.getActions().push(new StringBuilder("{}"));
		
		OperationResult operationResult = new OperationResult(opResult)
		operationResult.shouldBeLogged = shouldBeLogged
		
		commandProcessActionPackage.isInRestoreMode = isInRestoreMode
		
		when:
		this.commandProcessor.handleLoggingAndRollback(commandProcessActionPackage, operationResult)
		
		then:
		this.commandProcessor
		commandProcessActionPackage.undoActionBundle.getActions().size() == numberOfUndoActionsRemaining
		
		where:
		shouldBeLogged 	| isInRestoreMode	| opResult			| numberOfUndoActionsRemaining
		true			| false				| OpResult.SUCCESS	| 1
		false			| false				| OpResult.FAILURE	| 1
	}
	
	@Test
	def 'test commit transaction'() {
		
		given:
		CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageMother.getGoodOne(CommitTransactionCommand.class)
		CommitTransactionDto commitTransactionDto = new CommitTransactionDto()
		commitTransactionDto.tranId = new BigDecimal(123);
		
		when:
		def operationResult = this.commandProcessor.execute(commitTransactionDto, commandProcessActionPackage)
		
		then:
		operationResult.opResult == OpResult.SUCCESS
		
	}
}
