package com.fletch22.aop;

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

@Component
@Aspect
public class Log4EventRedoAspect {
	
	Logger logger = LoggerFactory.getLogger(Log4EventRedoAspect.class);

	@After("@annotation(com.fletch22.aop.Loggable4EventRedo)")
	public void loggingRedoSignature(JoinPoint joinPoint) {
		logger.info("Executing my REDO AFTER advice!");
		
		Object[] args = joinPoint.getArgs();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String methodName = signature.getMethod().getName();
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
		
		Annotation[] annotations = signature.getMethod().getAnnotations();
		for (Annotation annotation: annotations) {
			logger.info("Annotation found: {}", annotation.getClass());	
		}
		
		Loggable[] loggableArray = signature.getMethod().getAnnotationsByType(Loggable.class);
		logger.info("Loggables found on method: {}", loggableArray.length);
		
		loggableArray = signature.getDeclaringType().getClass().getAnnotationsByType(Loggable.class);
		logger.info("Loggables found on class: {}", loggableArray.length);
		
		logger.info("Target: {}", joinPoint.getTarget().getClass().getSimpleName());
		logger.info("This: {}", joinPoint.getThis().getClass().getSimpleName());
	}
	
	public static class ParameterPair {
		
		@Expose
		public String clazzName;
		
		@Expose
		public String jsonValue;
		
		public StringBuilder toJson() {
			
			Gson gson = new Gson();
			
			return new StringBuilder(gson.toJson(this));
		}
		
		public static ParameterPair fromJson(String json) {
			// Use an annotation value to identify the method so refactoring method name/location will not affect JSON deserialization.
			// Create Unit test that scans all class's methods for annotations and finds duplicates.
			throw new NotImplementedException("fromJson not finished yet.");
		}
	}
}
