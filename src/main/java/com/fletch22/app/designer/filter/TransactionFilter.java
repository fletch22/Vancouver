package com.fletch22.app.designer.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.command.transaction.TransactionService;

@WebFilter(filterName = "TransactionFilter", urlPatterns = {"/api/*"})
public class TransactionFilter implements Filter {

	Logger logger = LoggerFactory.getLogger(TransactionFilter.class);
	
	TransactionService transactionService;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	// NOTE: 03-12-2016: This uses a synchronized filter to prevent multiple accesses of the database at the same time. In other words,
	// this ensures that the database can be accessed only by 1 thread at a time.
	@Override
	public synchronized void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		TransactionService transactionService = getTransactionService();
		try {
			chain.doFilter(request, response);	
		} catch (Exception e) {
			logger.info("Encountered exception in filter chain.");
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (transactionService.isTransactionInFlight()) {
				logger.info("Found open transaction while finishing processing HTTP request. Auto-rolling back transaction.");
				transactionService.rollbackCurrentTransaction();
			}
		}
	}
	
	public TransactionService getTransactionService() {
		if (this.transactionService == null) {
			this.transactionService = Fletch22ApplicationContext.getApplicationContext().getBean(TransactionService.class);
		}
		return this.transactionService;
	}
	
	@Override
	public void destroy() {}
}
