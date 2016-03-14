package com.fletch22.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.util.IocUtil;

@Aspect
public class TransactionalAspect {

	Logger logger = LoggerFactory.getLogger(TransactionalAspect.class);
	
	public int transactionCounter = 0;
	
	TransactionService transactionService;

	@Pointcut("execution(@com.fletch22.aop.Transactional * *(..))")
	private void transactionalAssurance() {
	}

	@Around("transactionalAssurance()")
	public Object loggingAround(ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {
		Object retObject = null;
		
		logger.info("ogging from inside new ASPECT!!!!");
		
		try {
			
			if (transactionCounter == 0) {
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
			getTransactionService().rollbackCurrentTransaction();
			throw new RuntimeException(
					"Encountered problem. Rolling back transaction. Logging from aspect", e);
		}
		return retObject;
	}
	
	// @AfterThrowing(pointcut = "redoLogger()", throwing = "ex")
	// public void handleException(JoinPoint joinPoint, Throwable ex) {
	// }

	private IocUtil getIocUtil() {
		return (IocUtil) getBean(IocUtil.class);
	}

	private Object getBean(Class<?> clazz) {
		return Fletch22ApplicationContext.getApplicationContext()
				.getBean(clazz);
	}
	
	public TransactionService getTransactionService() {
		if (this.transactionService == null)  {
			this.transactionService = (TransactionService) getBean(TransactionService.class);
		}
		return this.transactionService;
	}
}
