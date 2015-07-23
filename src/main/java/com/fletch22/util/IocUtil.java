package com.fletch22.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fletch22.Fletch22ApplicationContext;

@Component
public class IocUtil {
	
	Logger logger = LoggerFactory.getLogger(IocUtil.class);
	
	public Class<?> getBeansSpringSingletonInterface(Object object) {
		ApplicationContext applicationContext = Fletch22ApplicationContext.getApplicationContext();
		
		Class<?> interfaze = null;
		
		Class<?>[] interfazeArray = object.getClass().getInterfaces();
		for (Class<?> clazz: interfazeArray) {
			
			int numberFound = applicationContext.getBeanNamesForType(clazz).length;
			if (numberFound == 1) {
				Object candidate = applicationContext.getBean(clazz);
				String clazzName = candidate.getClass().getName();
				if (object.getClass().getName().equals(clazzName)) {
					interfaze = clazz;
					break;
				}
			}
		}
		
		return interfaze;
	}
}
