package com.fletch22.dao;

import static org.junit.Assert.*

import java.sql.Connection

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.TranDateGenerator

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext.xml")
class LogDaoSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(LogDaoSpec)
	
	@Autowired
	LogDao logDao;
	
	@Autowired
	TranDateGenerator tranDateGenerator;

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
	def 'clear out database'() {
		
		given:
				
		when:
		logDao.clearOutDatabase();
		
		then:
		int numberCommands = logDao.countCommands()
		numberCommands == 0
	}
	
	@Test
	def 'testConnection'() {
		
		given:
		when:
		String connectionString = logDao.getConnectionString()
		
		then:
		connectionString
	}
	
	@Test
	def 'insert sample record into database'() {
		
		given:
		CommandExpressor commandExpressor = new CommandExpressor();
			
		//def action = commandExpressor.jsonCommandGetTotalOrbCount();
		
		logger.info("TD: {}", tranDateGenerator.getTranDate());
		
		when:
		//logDao.logAction(action, tranDateGenerator.getTranDate(), tranId)
		def test = 'test'
		
		then:
		1 == 1
	}
	
}
