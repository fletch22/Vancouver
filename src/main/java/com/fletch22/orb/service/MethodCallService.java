package com.fletch22.orb.service;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.command.orbType.dto.MethodCallDto;

@Component
public class MethodCallService {

	Logger logger = LoggerFactory.getLogger(MethodCallService.class);
	

	public Object process(MethodCallDto methodCallDto) {
		Object result = null;
		try {
			Class<?> clazz = Class.forName(methodCallDto.className);
			Object objectToExecute = Fletch22ApplicationContext.getApplicationContext().getBean(clazz);
	
			Method[] allMethods = clazz.getDeclaredMethods();
			for (Method method : allMethods) {
				String methodName = method.getName();
				logger.debug("Found method: '{}' while looking for method '{}'", methodName, methodCallDto.methodName);
				if (methodName.equals(methodCallDto.methodName)) {
					Type[] parameterTypeArray = method.getGenericParameterTypes();
					logger.info("Found parameter type length: '{}' while looking for parameterTypes array length '{}'", parameterTypeArray.length, methodCallDto.parameterTypes.length);
					
					if (parameterTypeArray.length == methodCallDto.parameterTypes.length) {
	
						boolean isWrongMethod = false;
						for (int i = 0; i < parameterTypeArray.length; i++) {
							
							Type parameter = parameterTypeArray[i];
							String parameterTypeName = methodCallDto.parameterTypes[i];
							
							if (!parameter.toString().equals(parameterTypeName)) {
								isWrongMethod = true; 
								break;
							}
						}
						
						if (isWrongMethod) {
							continue;
						}
						
						method.setAccessible(true);
						Object castObject = clazz.cast(objectToExecute);
						result = method.invoke(castObject, methodCallDto.args);
						String resultAsString = (result == null) ? "null": result.toString();
						logger.info("{}.{}() returned {}", methodCallDto.className, methodName, resultAsString);
						break;
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("Encountered problem while invoking method by reflection.", ex);
		}
		
		return result;
	}
	
	
}

