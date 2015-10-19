package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class GetCurrentTransactionIdCommandSpec extends Specification {
	
	@Autowired
	GetCurrentTransactionIdCommand getCurrentTransactionIdCommand

	def 'test GetCurrentTransactionId Command'() {
		
		given:
		def json = this.getCurrentTransactionIdCommand.toJson().toString()
		
		when:
		this.getCurrentTransactionIdCommand.fromJson(json)
		
		then:
		notThrown(Exception)
	}
}
