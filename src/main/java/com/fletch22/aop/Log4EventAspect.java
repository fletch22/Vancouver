package com.fletch22.aop;

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;

@Component
@Aspect
public class Log4EventAspect {
	
	Logger logger = LoggerFactory.getLogger(Log4EventAspect.class);
	
	private static final String CLASS_NAME = "className";
	private static final String METHOD_NAME = "methodName";
	private static final Object METHOD_PARAMETERS = "methodParameters";
	
	private static boolean logNextMethodCallAsUndo = false;

	@Around("@annotation(com.fletch22.aop.Loggable4Event)")
	public void loggingAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		logger.info("Inside logging around.");
		
		StringBuilder sb = convertCall(proceedingJoinPoint);
		
		if (!logNextMethodCallAsUndo) {
			Object retVal = proceedingJoinPoint.proceed();
		}
		
		logNextMethodCallAsUndo = false;
	}
	
	private StringBuilder convertCall(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String methodName = signature.getMethod().getName();
		String clazzName = joinPoint.getTarget().getClass().getName();
		
		StringBuilder sb = convertCallToJson(clazzName, methodName, args);
		logger.info("CN: {}: MN: {}, Nbr args: {}, json: {}", clazzName, methodName, args.length, sb.toString());
		
		return sb;
	}

//	@AfterThrowing(pointcut = "@annotation(com.fletch22.aop.Loggable4Event)", throwing = "ex")
//	public void handleException(JoinPoint joinPoint, Throwable ex) {
//		logNextMethodCallAsUndo = false;
//	}
	
	private StringBuilder convertCallToJson(String clazzName, String methodName, Object[] args) {
		StringBuilder translation = new StringBuilder();
		
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.METHOD_CALL);
		translation.append("\":{\"");
		translation.append(CLASS_NAME);
		translation.append("\":\"");
		translation.append(clazzName);
		translation.append("\"},{\"");
		translation.append(METHOD_NAME);
		translation.append("\":\"");
		translation.append(methodName);
		translation.append("\"},{\"");
		translation.append(METHOD_PARAMETERS);
		translation.append("\":[");
		
		boolean isFirstParameter = true;
		for (Object parameter: args) {
			JsonWrapper jsonWrapper = new JsonWrapper(parameter);
			if (!isFirstParameter) {
				translation.append(",");
			} 
			isFirstParameter = false;
			translation.append(jsonWrapper.toJson());
		}
		translation.append("]}}}");
		
		return translation;
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

	public static void logNextMethodCallAsUndo() {
		logNextMethodCallAsUndo = true;
	}
}
