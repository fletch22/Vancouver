package com.fletch22.app.designer.filter;

import static org.junit.Assert.assertFalse

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.command.transaction.RollbackTransactionService
import com.fletch22.orb.command.transaction.TransactionService

@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class TransactionFilterSpec extends Specification {
	
	TransactionFilter transactionFilter;
	
	@Autowired
	BeginTransactionService beginTransactionService;
	
	@Autowired
	RollbackTransactionService rollbackTransactionService;
	
	@Autowired
	TransactionService transactionService;
	
	def setup() {
		this.transactionFilter = new TransactionFilter();
	}
	
	def 'test filter rolls back open transaction'() {

		given:
			BigDecimal tranId = beginTransactionService.beginTransaction()
			HttpServletRequest request = Mock(HttpServletRequest)
			HttpServletResponse response = Mock(HttpServletResponse)
			FilterChain filterChain = Mock(FilterChain)
		when: 
			transactionFilter.doFilter(request, response, filterChain)
		
		then: 
			assertFalse(transactionService.isTransactionInFlight())
	}
}