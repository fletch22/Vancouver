package com.fletch22.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggableAspect {
	
	Logger logger = LoggerFactory.getLogger(LoggableAspect.class);

	@After("@annotation(com.fletch22.aop.Loggable)")
	public void loggable(JoinPoint joinPoint) {
		logger.info("Executing my advice!");
	}
}