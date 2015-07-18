package com.fletch22.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.command.MethodCallCommand;
import com.fletch22.orb.command.orbType.dto.MethodCallDto;
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder;

@Component
@Aspect
public class Log4EventAspect {
	
	Logger logger = LoggerFactory.getLogger(Log4EventAspect.class);
	
	public static boolean isPreventNextLineFromExecutingAndAddToUndoLog;
	
	@Pointcut("execution(@com.fletch22.aop.Loggable4Event * *(..))")
	private void redoLogger() {}
	
	@Around("redoLogger()")
	public Object loggingAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		logger.info("Inside logging around.");
		
		EventLogCommandProcessPackageHolder packageHolder = getPackageHolder();
		StringBuilder methodCallSerialized = convertCall(proceedingJoinPoint);
		
		logger.info("MCS: {}", methodCallSerialized);
		logger.info("Is packageHoldder null? {}", packageHolder.commandProcessActionPackage == null);
		
		Object retObject = null;
		if (Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog) {
			// NOTE: This captures the undo action for the undo log.
			BigDecimal tranDate = packageHolder.commandProcessActionPackage.getTranDate();
			packageHolder.commandProcessActionPackage.getUndoActionBundle().addUndoAction(methodCallSerialized, tranDate);
			Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = false;
		} else {
			// NOTE: This captures the redo action for the redo log.
			packageHolder.commandProcessActionPackage.setAction(methodCallSerialized);
			retObject = proceedingJoinPoint.proceed();
		}
		
		return retObject;
	}
	
	private EventLogCommandProcessPackageHolder getPackageHolder() {
		EventLogCommandProcessPackageHolder packageHolder = (EventLogCommandProcessPackageHolder) Fletch22ApplicationContext.getApplicationContext().getBean(EventLogCommandProcessPackageHolder.class);
		return packageHolder;
	}
	
	@AfterThrowing(pointcut = "redoLogger()", throwing = "ex")
	public void handleException(JoinPoint joinPoint, Throwable ex) {
		Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = false;
	}
	
	private StringBuilder convertCall(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String methodName = method.getName();
		Type[] parametersTypes = method.getGenericParameterTypes();
		String clazzName = joinPoint.getTarget().getClass().getName();
		
		StringBuilder sb = convertToJson(clazzName, methodName, parametersTypes, args);
		logger.debug("CN: {}: MN: {}, Nbr args: {}, json: {}", clazzName, methodName, args.length, sb.toString());
		
		return sb;
	}

	private StringBuilder convertToJson(String clazzName, String methodName, Type[] parameterTypes, Object[] args) {
		
		List<String> parameterTypeNames = new ArrayList<String>();
		for (Type type : parameterTypes) {
			parameterTypeNames.add(type.toString());
		}
		
		String[] paramTypeNames = new String[parameterTypeNames.size()];
		parameterTypeNames.toArray(paramTypeNames);
		
		MethodCallDto methodCallDto = new MethodCallDto(clazzName, methodName, paramTypeNames, args);
		return getMethodCallCommandBean().toJson(methodCallDto);
	}
	
	private MethodCallCommand getMethodCallCommandBean() {
		return Fletch22ApplicationContext.getApplicationContext().getBean(MethodCallCommand.class);
	}
	
	private void logDetails(ProceedingJoinPoint proceedingJoinPoint) {
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		Object[] args = proceedingJoinPoint.getArgs();
		
		Annotation[] annotations = signature.getMethod().getAnnotations();
		for (Annotation annotation: annotations) {
			logger.debug("Annotation found: {}", annotation.getClass());	
		}
		
		Loggable[] loggableArray = signature.getMethod().getAnnotationsByType(Loggable.class);
		logger.info("Loggables found on method: {}", loggableArray.length);
		
		loggableArray = signature.getDeclaringType().getClass().getAnnotationsByType(Loggable.class);
		logger.info("Loggables found on class: {}", loggableArray.length);
		
		logger.info("Target: {}", proceedingJoinPoint.getTarget().getClass().getSimpleName());
		logger.info("This: {}", proceedingJoinPoint.getThis().getClass().getSimpleName());
	}
	
	private String getCallString(MethodSignature signature, Object[] args) {
		Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
		
		String argumentString = StringUtils.EMPTY;
		if (parameterTypes.length > 0) {
			boolean isFirst = true;
			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameter = parameterTypes[i];
				if (!isFirst) argumentString += ", ";
				isFirst = false;
				String parameterName = parameter.getName();
				if (parameterName.equals(String.class.getName())) {
					argumentString += "\"" + args[i] + "\"";
				} else if (parameterName.equals(Long.class.getName())) {
					argumentString += args[i] + "L";
				} else {
					throw new RuntimeException("Encountered problem understanding parameter type: " + parameterName);
				}
			}
		}
		
		return argumentString;
	}
	
	public static void preventNextLineFromExecutingAndLogTheUndoAction() {
		isPreventNextLineFromExecutingAndAddToUndoLog = true;
	}
}
