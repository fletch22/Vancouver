package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class KillTransactionSpec extends Specification {
	
	@Autowired
	KillTransactionCommand killTransactionCommand

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
