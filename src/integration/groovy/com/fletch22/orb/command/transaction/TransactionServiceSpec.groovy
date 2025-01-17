package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import com.fletch22.dao.LogActionDao
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.client.service.OrbTypeService

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class TransactionServiceSpec extends Specification {
	
	@Shared Logger logger = LoggerFactory.getLogger(TransactionServiceSpec.class);

	@Autowired
	TransactionService transactionService
	
	@Shared BigDecimal tranDateDefault = new BigDecimal("123.0000004")
	
	@Autowired
	OrbTypeService orbTypeService
	
	@Autowired
	LogActionDao logActionDao
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	BeginTransactionService beginTransactionService
	
	@Autowired
	Cache cache;
	
	def setup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
		transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
	}
	
	def cleanup() {
		this.transactionService.rollbackCurrentTransaction();
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	@Test
	def 'test tran id generator no transaction in flight'() {
	
		given:
		assert transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
		
		when:
		def tranId = this.transactionService.generateTranId()
	
		then:
		tranId
		transactionService.@transactionIdInFlight == TransactionService.NO_TRANSACTION_IN_FLIGHT
	}
	
	@Test
	def 'test tran id generator while a transaction in flight'() {
	
		given:
		transactionService.@transactionIdInFlight = new BigDecimal("123")
		
		when:
		def tranId = this.transactionService.generateTranId()
	
		then:
		tranId.compareTo(transactionService.@transactionIdInFlight) != 0
	}
	
	@Unroll
	@Test
	def 'test begin and rollback transaction'() {
		
		given:
		transactionService.@transactionIdInFlight = TransactionService.NO_TRANSACTION_IN_FLIGHT
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
	
	@Unroll
	@Test
	def 'test begin and commit transaction'() {
		
		given:
		transactionService.@transactionIdInFlight = TransactionService.NO_TRANSACTION_IN_FLIGHT
		assert !this.transactionService.isTransactionInFlight()
		
		BigDecimal tranId = 123;
		this.transactionService.beginTransaction(tranId);
		
		assert this.transactionService.isTransactionInFlight()
		
		orbTypeService.addOrbType("label_testbeginandcommittransaction")
		
		when:
		this.transactionService.commitTransaction();
		
		then:
		notThrown(Exception)
		assert !this.transactionService.isTransactionInFlight()
	}
	
//	@Test
//	def 'test rollback to specified transaction transaction'() {
//		
//		given:
//		transactionService.@transactionIdInFlight = TransactionService.NO_TRANSACTION_IN_FLIGHT
//		assert !this.transactionService.isTransactionInFlight()
//		
//		BigDecimal tranIdFirst = this.transactionService.beginTransaction();
//		orbTypeService.addOrbType("firstType")
//		
//		this.transactionService.commitTransaction();
//		
//		BigDecimal tranIdSecond = this.transactionService.beginTransaction();
//		orbTypeService.addOrbType("secondType")
//		
//		this.transactionService.commitTransaction();
//		
//		when:
//		this.transactionService.rollback
//		
//		then:
//		notThrown(Exception)
//		assert !this.transactionService.isTransactionInFlight()
//	}
	
	@Test
	def 'test calling begin transaction when another transaction already in flight'() {
		
		given:
		BigDecimal tranId = 123
		this.transactionService.beginTransaction(tranId)
		
		when:
		BigDecimal tranIdTooSoon = 234
		this.transactionService.beginTransaction()
		
		then:
		Exception e = thrown(Exception)
		e.getMessage().contains("There is already a transaction underway ")
	}
	
	@Test
	def 'test calling call foo'() {
		
		given:
		logActionDao.recordTransactionStart(transactionService.generateTranId());
		
		when:
		BigDecimal tranId = 123
		this.transactionService.beginTransaction(tranId)
		
		then:
		Exception e = thrown(Exception)
	}
}
