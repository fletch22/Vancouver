package com.fletch22.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Log4EventUndoAspect {

	Logger logger = LoggerFactory.getLogger(Log4EventUndoAspect.class);

	// @Around("@annotation(com.fletch22.aop.Loggable4EventUndo)")
	// @Around("execution(* com.fletch22.aop.*.*(..))") // works!
	// @Around("within(com.fletch22.aop.*)") // works!
	//@Around("within(com.fletch22.aop.undo.*)") // works!
	@Around("execution(* com.fletch22.aop.undo.*.*(..))")
	public Object execute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		logger.info("Executing my UNDO event AFTER advice!");
		
		logger.info("Target: {}", proceedingJoinPoint.getTarget().getClass().getSimpleName());
		
		// start stopwatch
		//Object retVal = pjp.proceed();
		// stop stopwatch
		//return retVal;
		return null;
	}
}
