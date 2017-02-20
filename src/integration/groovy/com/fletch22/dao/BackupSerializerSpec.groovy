package com.fletch22.dao;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class BackupSerializerSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(BackupSerializerSpec)
	
	@Autowired
	BackupSerializer backupSerializer

	@Test
	def 'test serialize'() {
		
		given:
		def backupFileCoderMock = Mock(BackupFileCoder)
		backupSerializer.backupFileCoder = backupFileCoderMock
		backupSerializer.backupFileCoder.encode(_) >> { StringBuilder e -> 
			return e
		} 
		
		ActionUndoInfo actionUndoInfo = getActionUndoInfo1()
		
		when:
		def sb = backupSerializer.serializeRecord(actionUndoInfo)
		
		then:
		sb != null
		sb.toString() == '{"action":{"someAction":true},"undoAction":{"someUndoAction":1234},"tranDate":"1000000.000000012","tranId":"1000000.000002342355"}'
	}
	
	@Test
	def 'test deserialize'() {
		
		given:
		ActionUndoInfo actionUndoInfoExpected = getActionUndoInfo1();
		StringBuilder record = backupSerializer.serializeRecord(actionUndoInfoExpected)
		
		logger.info("Record: {}", record);
		
		def backupFileCoderMock = Mock(BackupFileCoder)
		backupSerializer.backupFileCoder = backupFileCoderMock
		backupSerializer.backupFileCoder.decode(_) >> { StringBuilder e -> 
			return e
		}
		
		backupSerializer.backupFileCoder.encode(_) >> { StringBuilder e ->
			return e
		}
		
		logger.info('bs null? {}', backupSerializer == null)
		
		when:
		def actionUndoInfoActual = backupSerializer.deserializeRecord(record)
		
		then:
		actionUndoInfoActual != null
		actionUndoInfoExpected.action.toString() == actionUndoInfoActual.action.toString();
	}

	private ActionUndoInfo getActionUndoInfo1() {
		ActionUndoInfo actionUndoInfo = new ActionUndoInfo()
		actionUndoInfo.action = new StringBuilder('{"someAction":true}')
		actionUndoInfo.undoAction = new StringBuilder('{"someUndoAction":1234}')
		actionUndoInfo.tranDate = new BigDecimal('1000000.000000012')
		actionUndoInfo.tranId = new BigDecimal('1000000.000002342355')
		return actionUndoInfo
	}
}
