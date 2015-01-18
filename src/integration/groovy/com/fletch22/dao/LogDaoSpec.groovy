package com.fletch22.dao;

import static org.junit.Assert.*

import java.sql.Connection

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext.xml")
class LogDaoSpec extends Specification {
	
	@Autowired
	LogDao logDao;

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
	
	@Test
	def 'testCountCommands'() {
		
		given:
		
		when:
		Connection connection = logDao.countCommands()
		
		then:
		true
	}
}
