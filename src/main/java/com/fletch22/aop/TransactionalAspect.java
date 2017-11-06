package com.fletch22.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.command.transaction.RollbackTransactionService;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.util.IocUtil;

@Aspect
public class TransactionalAspect {

	Logger logger = LoggerFactory.getLogger(TransactionalAspect.class);

	public int transactionCounter = 0;

	TransactionService transactionService;
	
	RollbackTransactionService rollbackTransactionService;
	
	@Pointcut("execution(@com.fletch22.aop.Transactional * *(..))")
	private void transactionalAssurance() {
	}

	@Around("transactionalAssurance()")
	public Object loggingAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object retObject = null;
		logger.debug("Logging from transactional aspect.");

		try {
			logger.debug("Current counter {}", transactionCounter);
			if (transactionCounter == 0) {
				logger.debug("beginning tran.");
				getTransactionService().beginTransaction();
			}

			transactionCounter++;
			retObject = proceedingJoinPoint.proceed();
			transactionCounter--;

			if (transactionCounter == 0) {
				logger.debug("Commiting transaction.");
				getTransactionService().commitTransaction();
			}
		} catch (Exception e) {
			transactionCounter = 0;
			getRollbackTransactionService().rollbackCurrentTransaction();
			throw new RuntimeException("Encountered problem. Rolling back transaction. Logging from aspect", e);
		}
		return retObject;
	}

	private IocUtil getIocUtil() {
		return (IocUtil) getBean(IocUtil.class);
	}

	private Object getBean(Class<?> clazz) {
		return Fletch22ApplicationContext.getApplicationContext().getBean(clazz);
	}

	public TransactionService getTransactionService() {
		if (this.transactionService == null) {
			this.transactionService = (TransactionService) getBean(TransactionService.class);
		}
		return this.transactionService;
	}
	
	public RollbackTransactionService getRollbackTransactionService() {
		if (this.rollbackTransactionService == null) {
			this.rollbackTransactionService = (RollbackTransactionService) getBean(RollbackTransactionService.class);
		}
		return this.rollbackTransactionService;
	}
}
