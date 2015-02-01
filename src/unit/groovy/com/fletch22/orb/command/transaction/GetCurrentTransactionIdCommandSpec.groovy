package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext.xml')
class GetCurrentTransactionIdCommandSpec extends Specification {
	
	@Autowired
	GetCurrentTransactionIdCommand getCurrentTransactionIdCommand

	@Test
	def 'test GetCurrentTransactionId Command'() {
		
		given:
		def json = this.getCurrentTransactionIdCommand.toJson().toString()
		
		when:
		this.getCurrentTransactionIdCommand.fromJson(json)
		
		then:
		notThrown(Exception)
	}
}
