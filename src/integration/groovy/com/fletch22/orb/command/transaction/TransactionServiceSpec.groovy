package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import com.fletch22.orb.IntegrationTests;
import com.fletch22.orb.TranDateGenerator

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class TransactionServiceSpec extends Specification {

	@Autowired
	TransactionService transactionService
	
	@Shared BigDecimal tranDateDefault = new BigDecimal("123.0000004")
	
	def setup() {
		this.transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
	}

	@Test
	def 'test tran id generator no transaction in flight'() {
	
		given:
		assert this.transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
		
		when:
		def tranId = this.transactionService.generateTranId()
	
		then:
		tranId
		this.transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
	}
	
	@Test
	def 'test tran id generator while a transaction in flight'() {
	
		given:
		this.transactionService.@transactionIdInFlight = new BigDecimal("123")
		
		when:
		def tranId = this.transactionService.generateTranId()
	
		then:
		tranId.compareTo(this.transactionService.@transactionIdInFlight) != 0
	}
	
	@Unroll
	@Test
	def 'test begin and rollback transaction'() {
		
		given:
		this.transactionService.@transactionIdInFlight = TransactionService.NO_TRANSACTION_IN_FLIGHT
		assert !this.transactionService.isTransactionInFlight()
		
		BigDecimal tranId = 123;
		this.transactionService.beginTransaction(tranId);
		
		assert this.transactionService.isTransactionInFlight()
		
		when:
		this.transactionService.rollbackCurrentTransaction();
		
		then:
		notThrown(Exception)
		assert !this.transactionService.isTransactionInFlight()
	}
}
