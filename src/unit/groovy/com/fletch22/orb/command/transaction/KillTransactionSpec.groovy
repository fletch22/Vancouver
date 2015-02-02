package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class KillTransactionSpec extends Specification {
	
	@Autowired
	KillTransactionCommand killTransactionCommand

	@Test
	def 'test KillTransaction'() {
		
		given:
		def transactionIdExpected = 1.00002
		def action = this.killTransactionCommand.toJson(transactionIdExpected);
		
		when:
		def actionData = this.killTransactionCommand.fromJson(action.toString());
		
		then:
		actionData.transactionId == transactionIdExpected;
	}
}
