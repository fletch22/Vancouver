package com.fletch22.dao;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.command.CommandBundle
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory
import com.fletch22.orb.command.processor.CommandProcessor
import com.fletch22.orb.command.processor.OperationResult
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage
import com.fletch22.orb.rollback.UndoAction
import com.fletch22.orb.rollback.UndoActionBundle

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class LogActionServiceSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(LogActionServiceSpec)
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	@Autowired
	LogActionService logActionService
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	CommandProcessor commandProcessor
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	def setup() {
		initializer.nukeAndPaveAllIntegratedSystems()
	}

	@Unroll
	@Test
	def 'test get result set from CommandBundle'() {
		
		given:
		setup()
		
		CommandBundle commandBundle = new CommandBundle();
		
		CommandProcessActionPackage commandProcessActionPackage = null
		for (i in 0..numberOfAdds.intValue()) {
			def json = addOrbTypeCommand.toJson('foo')
			commandBundle.addCommand(json);
		}
		
		commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
		OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
		
		when:
		UndoActionBundle undoActionBundle = logActionService.getUndoActions(commandProcessActionPackage.getTranId().longValue())
		
		then:
		undoActionBundle
		undoActionBundle.getActions().size() == 1
		
		where:
		numberOfAdds << [1, 5]
	}
	
	// Single loggable transactions will be written to the log at once and will get a unique tran id for each inserted row.
	// However multiple operations during a rollbackable transaction should all get/user the same tran ID; the DB does not allow multiple
	// rows with the same tran ID. Therefore all the rollback undo operations should be inserted into a single record and only on commit.
	// A cursor like transaction container will be instantiated whenever a client intiates a transaction. During the cursor's lifetime, all 
	// operations will add undo operations to the cursor. On commit the undo operations will be written to the DB.
	
	@Unroll
	@Test
	def 'test get result set from multiple actions same tran date'() {
		
		given:
		setup()
		
		BigDecimal tranId;
		CommandBundle commandBundle = new CommandBundle();
		
		CommandProcessActionPackage commandProcessActionPackage = null
		for (i in 1..numberOfAdds.intValue()) {
			def json = addOrbTypeCommand.toJson('foo')
			commandBundle.addCommand(json);
			commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(commandBundle.toJson())
			if (null != tranId) {
				commandProcessActionPackage.setTranId(tranId);
			}
			
			logger.info('tranDate {}', commandProcessActionPackage.getTranDate().toString());
			logger.info('tranId {}', tranId);
			
			OperationResult operationResult = this.commandProcessor.processAction(commandProcessActionPackage)
			
			if (null == tranId) {
				tranId = commandProcessActionPackage.getTranId()
			}
		}
		
		when:
		UndoActionBundle undoActionBundle = logActionService.getUndoActions(commandProcessActionPackage.getTranId().longValue())
		
		then:
		undoActionBundle
		undoActionBundle.getActions().size() == numberOfAdds
		
		long lastTranDate = 0
		def isFirst = true
		while (!undoActionBundle.getActions().empty()) {
			UndoAction undoAction = undoActionBundle.getActions().pop()
			logger.info("TranDate {}", undoAction.tranDate.toString())
			if (!isFirst) {
				assertTrue(lastTranDate > undoAction.tranDate.longValue())
			}
			isFirst = false
			lastTranDate = undoAction.tranDate.longValue()
		}
		
		where:
		numberOfAdds << [1, 5, 120]
	}
}
