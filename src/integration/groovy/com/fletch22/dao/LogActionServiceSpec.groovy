package com.fletch22.dao;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.TranDateGenerator
import com.fletch22.orb.client.service.OrbTypeService;
import com.fletch22.orb.command.ActionSniffer
import com.fletch22.orb.command.CommandBundle
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.OperationResult
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage
import com.fletch22.orb.command.processor.OperationResult.OpResult
import com.fletch22.orb.command.transaction.TransactionService
import com.fletch22.orb.rollback.UndoAction
import com.fletch22.orb.rollback.UndoActionBundle

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class LogActionServiceSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(LogActionServiceSpec)
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	LogActionService logActionService
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	CommandProcessor commandProcessor
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	@Autowired
	ActionSniffer actionSniffer;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	LogActionDao logActionDao
	
	@Autowired
	OrbTypeService orbTypeService
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def cleanup() {
		transactionService.rollbackCurrentTransaction()
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	@Unroll
	@Test
	def 'test get result set from CommandBundle'() {
		
		given:
		setup()
		
		CommandBundle commandBundle = new CommandBundle();
		
		CommandProcessActionPackage commandProcessActionPackage = null
		for (i in 0..numberOfAdds.intValue()) {
			def json = addOrbTypeCommand.toJson('foo' + i)
			commandBundle.addCommand(json);
		}
		
		commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		when:
		List<UndoActionBundle> undoActionBundleList = logActionService.getUndoActionsForTransactionsAndSubsequent(commandProcessActionPackage.getTranId().longValue())
		
		then:
		undoActionBundleList
		undoActionBundleList.size() == 1
		UndoActionBundle undoActionBundle = undoActionBundleList.get(0)
		
		logger.debug(undoActionBundle.toJson().toString());
		
		where:
		numberOfAdds << [1, 5000]
	}
	
	@Unroll
	@Test
	def 'test get undos after transaction'() {
		
		given:
		setup()
		
		CommandBundle commandBundle = new CommandBundle();
		
		CommandProcessActionPackage commandProcessActionPackage = null
		for (i in 0..numberOfAdds.intValue()) {
			def json = addOrbTypeCommand.toJson('foo' + i)
			commandBundle.addCommand(json);
		}
		
		commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		when:
		List<UndoActionBundle> undoActionBundleList = logActionService.getUndoActionsForSubsequentTransactions(commandProcessActionPackage.getTranId())
		
		then:
		undoActionBundleList == []
		undoActionBundleList.size() == 0
		
		where:
		numberOfAdds << [1, 3]
	}
	

	def printActionList(List<UndoActionBundle> undoActionBundleList) {
		if (undoActionBundleList.size() == 0) {
			println 'nothing in bundle.'
		}
		
		for (UndoActionBundle undoActionBundle : undoActionBundleList) {
			for (UndoAction undoAction : undoActionBundle.actions) {
				println undoAction.tranDate.toString() + ": " + undoAction.action
			}
		}
	}
	
	
	@Unroll
	@Test
	def 'test get result set from multiple actions same tran date'() {
		
		given:
		setup()
		
		BigDecimal tranId;
		
		CommandProcessActionPackage commandProcessActionPackage = null
		commandProcessActionPackage = insertTypes(numberOfAdds, commandProcessActionPackage)
		
		when:
		List<UndoActionBundle> undoActionBundleList = logActionService.getUndoActionsForTransactionsAndSubsequent(commandProcessActionPackage.getTranId().longValue())
		
		then:
		undoActionBundleList
		UndoActionBundle undoActionBundle = undoActionBundleList.get(0)
		
		long lastTranDate = 0
		def isFirst = true
		while (!undoActionBundle.getActions().empty()) {
			UndoAction undoAction = undoActionBundle.getActions().pop()
			if (!isFirst) {
				assertTrue(lastTranDate > undoAction.tranDate.longValue())
			}
			isFirst = false
			lastTranDate = undoAction.tranDate.longValue()
			
			UndoActionBundle.fromJson(new StringBuilder(undoAction.action));
			
			logger.debug(undoAction.action.toString());
		}
		
		where:
		numberOfAdds << [1, 5, 10]
	}
	
	@Test
	def 'testThrowException if new transaction started'() {
		
		given:
		setup()
		
		this.logActionDao.recordTransactionStart(this.transactionService.generateTranId());
		
		when:
		this.transactionService.beginTransaction(123);
		
		then:
		thrown Exception
	}
	
	@Test
	def 'testLoadLogAction'() {
		
		given:
		setup()
		
		String jsonAddOrb = addOrbTypeCommand.toJson("testLabel")
		
		CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(new StringBuilder(jsonAddOrb));
		
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage);
		
		when:
		List<String> allActionsList = this.logActionDao.getAllActions()
		
		for (String action : allActionsList) {
			logger.debug(action);
		}
		
		then:
		operationResult.opResult == OpResult.SUCCESS
		allActionsList.size() > 0
	}
	
	@Test
	def 'testLoadLoadDb'() {
		
		given:
		setup()
		
		String jsonAddOrb = addOrbTypeCommand.toJson("testLabel")
		
		CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstance(new StringBuilder(jsonAddOrb));
		
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage);
		
		when:
		List<String> allActionsList = this.logActionDao.getAllActions()
		
		for (String action : allActionsList) {
			logger.debug(action);
		}
		
		logger.debug("Result: {}", operationResult.operationResultException.toString());
				
		then:
		operationResult.opResult == OpResult.SUCCESS
		allActionsList.size() > 0
	}

		
	private CommandProcessActionPackage insertTypes(Integer numberOfAdds, CommandProcessActionPackage commandProcessActionPackage) {
		BigDecimal tranId;
		int count = 0;
		for (i in 1..numberOfAdds.intValue()) {
			def json = addOrbTypeCommand.toJson('foo' + i)
			
			CommandBundle commandBundle = new CommandBundle()
			
			commandBundle.addCommand(json);
			commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
			if (null != tranId) { 
				commandProcessActionPackage.setTranId(tranId)
			}
			
			OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
			count++;
			
			if (null == tranId) {
				tranId = commandProcessActionPackage.getTranId()
			}
		}
		
		println "Number types inserted: ${count}"
		
		return commandProcessActionPackage
	}
}
