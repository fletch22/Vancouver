package com.fletch22.aop;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.Fletch22ApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class Log4EventAspectTest {

	static Logger logger = LoggerFactory.getLogger(Log4EventAspectTest.class);
	
	@Autowired
	Dog dog;
	
	@Test
	public void test() {

		// Arrange
		logger.info("test logger.");
		
		// Act
//		dog.bark();
		dog.runForwards(true);
		//dog.runBackwards();
	}

	@Component
	public static class Dog {
		
		Dog proxiedDog;
		
		@Loggable4Event
		public void bark() {
			logger.info("Inside the bark method.");
		}
		
		@Loggable4Event
		public void runForwards(boolean hasStickInMouth) {
			logger.info("Inside run forward method.");
			
			proxiedDog = Fletch22ApplicationContext.getApplicationContext().getBean(Dog.class);
			
			Log4EventAspect.logNextMethodCallAsUndo();
			proxiedDog.runBackwards();
		}
		
		private void setProxiedDog() {
			if (proxiedDog == null) {
				proxiedDog = Fletch22ApplicationContext.getApplicationContext().getBean(Dog.class);
			}
		}
		
		@Loggable4Event
		public void runBackwards() {
			logger.info("Inside dog run backwards method.");
		}
	}
}

