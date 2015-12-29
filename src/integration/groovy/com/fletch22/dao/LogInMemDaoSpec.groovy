package com.fletch22.dao;

import static org.junit.Assert.*

import java.sql.Connection

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.TranDateGenerator
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand
import com.fletch22.orb.command.transaction.TransactionService

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class LogInMemDaoSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(LogInMemDaoSpec)
	
	@Autowired
	LogActionInMemDao logDao;
	
	@Test
	def 'test is connection openable'() {
		
		given:
		
		when:
		Connection connection = logDao.getConnection()
		
		then:
		logDao.isConnectionOpen()
	}
	
	@Test
	def 'test is connection closed'() {
		
		given:
		
		when:
		Connection connection = logDao.getConnection()
		
		connection.close()
		
		then:
		connection
		false == logDao.isConnectionOpen()
	}
}
