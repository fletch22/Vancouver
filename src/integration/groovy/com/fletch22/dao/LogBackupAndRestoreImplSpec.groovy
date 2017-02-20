package com.fletch22.dao;

import static org.junit.Assert.*
import static org.mockito.Mockito.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.dao.BackupFileOperations.BackupFileWriter
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbTypeManager

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class LogBackupAndRestoreImplSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(LogBackupAndRestoreImplSpec)
	
	@Autowired
	LogBackupAndRestore logBackupAndRestore;
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Shared
	LogBackupAndRestoreImpl logBackupAndRestoreImpl
	
	@Shared
	LogActionDao logActionDaoOriginal
	
	@Shared
	BackupFileOperations backupFileOperationsOriginal
	
	@Shared
	BackupSerializer backupSerializerOriginal
	
	def setup() {
		this.logBackupAndRestoreImpl = this.logBackupAndRestore
		
		this.logActionDaoOriginal = this.logBackupAndRestoreImpl.logActionDao
		this.backupFileOperationsOriginal = this.logBackupAndRestoreImpl.backupFileOperations
		this.backupSerializerOriginal = this.logBackupAndRestoreImpl.backupSerializer
	}
	
	def teardown() {
		this.logBackupAndRestoreImpl.logActionDao = this.logActionDaoOriginal
		this.logBackupAndRestoreImpl.backupFileOperations = this.backupFileOperationsOriginal
		this.logBackupAndRestoreImpl.backupSerializer = this.backupSerializerOriginal
	}

	@Test
	def 'test persist to disk'() {
		
		given:
		def logActionDaoMock = Mock(LogActionDao)
		this.logBackupAndRestoreImpl.logActionDao = logActionDaoMock
		
		List<ActionUndoInfo> auiList = new ArrayList<ActionUndoInfo>()
		ActionUndoInfo aui = new ActionUndoInfo()
		aui.action = new StringBuilder('foo');
		auiList.add(aui)
		this.logBackupAndRestoreImpl.logActionDao.getAllActionsWithAssociatedUndos() >> auiList
		
		def backupFileOperationsMock = Mock(BackupFileOperations)
		this.logBackupAndRestoreImpl.backupFileOperations = backupFileOperationsMock
		
		def backupFileWriter = new BackupFileWriter(null, null)
		this.logBackupAndRestoreImpl.backupFileOperations.getBackupFileWriter() >> backupFileWriter
		
		def fileWriterMock = Mock(FileWriter)
		backupFileWriter.fileWriter = fileWriterMock
		
		def backupSerializerMock = Mock(BackupSerializer)
		this.logBackupAndRestoreImpl.backupSerializer = backupSerializerMock
				
		StringBuilder test = new StringBuilder("foo")
		backupSerializerMock.serializeRecord(_, _) >> test

		when:
		this.logBackupAndRestoreImpl.persistToDisk();
		
		then:
		this.logBackupAndRestoreImpl != null
		1 * fileWriterMock.write('foo')
		1 * fileWriterMock.write('\r\n')
	}
	
	def 'test integration with file system'() {
		
		given:
		orbTypeManager.createOrbType('foo234423', null)
		
		when:
		this.logBackupAndRestoreImpl.persistToDisk()
		
		then:
		noExceptionThrown()
	}
}
