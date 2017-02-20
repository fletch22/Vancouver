package com.fletch22.dao;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class BackupFileCoderSpec extends Specification {

	static Logger logger = LoggerFactory.getLogger(LogBackupAndRestoreImplSpec)
	
	@Autowired
	BackupFileCoder backupFileCoder
	
	@Unroll
	@Test
	def 'test encode'() {
		
		given:
		StringBuilder sb = new StringBuilder(value)
		
		when:
		sb = backupFileCoder.encode(sb);
		
		then:
		sb != null
		result == sb.toString()
		
		where:
		value 	  	   | result
		'foo'	  	   | 'foo'
		'foo\r\n' 	   | 'foo&lineFeed;&newLine;'
		'foo&\r\n' 	   | 'foo&amp;&lineFeed;&newLine;'
		'foo&amp;\r\n' | 'foo&amp;amp;&lineFeed;&newLine;'
	}
	
	@Unroll
	@Test
	def 'test decode'() {
		
		given:
		StringBuilder sb = new StringBuilder(value)
		
		when:
		sb = backupFileCoder.decode(sb);
		
		then:
		sb != null
		result == sb.toString()
		
		where:
		result 	  	   | value
		'foo'	  	   | 'foo'
		'foo\r\n' 	   | 'foo&lineFeed;&newLine;'
		'foo&\r\n' 	   | 'foo&amp;&lineFeed;&newLine;'
		'foo&amp;\r\n' | 'foo&amp;amp;&lineFeed;&newLine;'
	}
}
