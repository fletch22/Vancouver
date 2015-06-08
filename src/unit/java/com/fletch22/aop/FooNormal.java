package com.fletch22.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("fooNormal")
public class FooNormal implements Foo {
	
	Logger logger = LoggerFactory.getLogger(FooNormal.class);
		
	@Autowired
	@Qualifier("foo4Logging")
	Foo foo4Logging;
	
	@Loggable
	public void bar() {
		logger.info("Inside bar method.");
	}
	
	public void bar2() {
		logger.info("Inside bar2 method.");
	}
	
	@Loggable4EventRedo
	public void bar3() {
		logger.info("Inside bar3 method.");
	}
	
	@Loggable4EventRedo
	public void bar4(String banana, Long numberOfSeeds) {
		logger.info("Inside bar4 method.");
		
		foo4Logging.bar3();
	}
}
