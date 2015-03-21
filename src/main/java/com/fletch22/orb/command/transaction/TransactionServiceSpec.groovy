package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.joda.time.DateTime
import org.junit.Test
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.TranDateGenerator

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class TransactionServiceSpec extends Specification {

	@Shared TransactionService transactionService
	
	@Shared BigDecimal tranDateDefault = new BigDecimal("123.0000004")
	
	def setup() {
		this.transactionService = new TransactionService()
		TranDateGenerator tranDateGenerator = Mock()
		this.transactionService.tranDateGenerator = tranDateGenerator
		
		tranDateGenerator.getTranDate() >> tranDateDefault
	}
	
	@Test
	def 'test tran id generator no transaction in flight'() {
	
		given:
		assert this.transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
		
		when:
		def tranId = this.transactionService.getTranId()
	
		then:
		tranId
		this.transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
	}
	
	@Test
	def 'test tran id generator a transaction in flight'() {
	
		given:
		this.transactionService.@transactionIdInFlight = new BigDecimal("123")
		
		when:
		def tranId = this.transactionService.getTranId()
	
		then:
		tranId.compareTo(this.transactionService.@transactionIdInFlight) == 0
	}
	
}
