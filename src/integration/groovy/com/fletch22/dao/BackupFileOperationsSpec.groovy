package com.fletch22.dao;

import static org.junit.Assert.*

import java.io.File;

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class BackupFileOperationsSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(BackupFileOperationsSpec)
	
	@Autowired
	BackupFileOperations backupFileOperations

	@Test
	def 'test get backup file path'() {
		
		given:
		def expectedFilepath = 'C:\\Windows\\Temp'
		backupFileOperations.backupParentFolder = expectedFilepath
		
		when:
		def backupFilepath = backupFileOperations.getBackupFilepath() 
		
		then:
		backupFileOperations.backupParentFolder != null
		backupFilepath == expectedFilepath + "\\backup.txt"
	}
	
	@Test
	def 'test get parent folder path'() {
		
		given:
		def expectedFilepath = 'C:\\Windows\\Temp'
		backupFileOperations.backupParentFolder = expectedFilepath;
		
		when:
		def parentFolderPath = backupFileOperations.getParentFolderPath()
		
		then:
		backupFileOperations.backupParentFolder != null
		parentFolderPath == expectedFilepath
	}
}
