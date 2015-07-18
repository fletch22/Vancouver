package com.fletch22.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggableAspect {
	
	Logger logger = LoggerFactory.getLogger(LoggableAspect.class);
	
	@Before("@annotation(com.fletch22.aop.Loggable)")
	public void beforeMethodExecutes(JoinPoint joinPoint) {
		logger.info("Before executing the method!");
		
		
	}

	@After("@annotation(com.fletch22.aop.Loggable)")
	public void afterMethodExecutes(JoinPoint joinPoint) {
		logger.info("After executing the method!");
	}
	
	@AfterThrowing("@annotation(com.fletch22.aop.Loggable)")
	public void afterThrowingException(JoinPoint joinPoint) {
		logger.info("After throwing exception!");
	}
}