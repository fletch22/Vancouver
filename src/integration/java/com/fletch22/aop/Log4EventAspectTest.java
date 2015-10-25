package com.fletch22.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.util.IocUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class Log4EventAspectTest {

	static Logger logger = LoggerFactory.getLogger(Log4EventAspectTest.class);
	
	@Autowired
	@Qualifier("BorderCollie")
	Dog dog;
	
	@Autowired
	IocUtil iocUtil;
	
	@Test
	public void testSuccess() {
		
		// Arrange
		int numRuns = 100;
		
		// Act
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (int i = 0; i < numRuns; i++) {
			dog.runForwards(true, false);
		}
		stopWatch.stop();
		
		BigDecimal millis = new BigDecimal(stopWatch.getNanoTime()).divide(new BigDecimal(1000000)).divide(new BigDecimal(String.valueOf(numRuns)));
		
		logger.debug("millis per method call: {}", millis.toString());
		
		assertFalse("Execution prevent should have been reset to false.", Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog);
	}
	
	@Test
	public void testAOPWeaverLoaded() {
		assertTrue("AspectJWeaver should have been loaded.", isAspectJAgentLoaded());
	}
	
	public static boolean isAspectJAgentLoaded() {
	    try {
	        org.aspectj.weaver.loadtime.Agent.getInstrumentation();
	    } catch (NoClassDefFoundError | UnsupportedOperationException e) {
	    	logger.debug("Exception: ", e);
	        return false;
	    }
	    return true;
	}
	
	@Test
	public void testExceptionThrown() {

		// Arrange
		// Act
		boolean wasExceptionThrown = false;
		try {
			dog.runForwards(true, true);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		assertTrue("Exception should have been thrown.", wasExceptionThrown);
		assertFalse("Execution prevent should have been reset to false.", Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog);
	}
	
	@Test
	public void testInterfaceToImplementationMapping() {
		
		// Arrange
		// Act
		Class<?> interfaze = iocUtil.getBeansSpringSingletonInterface(dog);
		
		// Assert
		assertEquals(interfaze, Dog.class);
	}
	
	public interface Dog {
		public void runForwards(boolean hasStickInMouth, boolean throwException);
		public void runBackwards();
		public void dancing(Map<String, String> thing);
	}
	
	@SuppressWarnings("serial")
	@Component("BorderCollie")
	public static class BorderCollie implements Serializable, Dog  {
		
		@Loggable4Event
		public void bark() {
			logger.debug("Inside the bark method.");
		}
		
		@Loggable4Event
		public void dancing(Map<String, String> thing) {
			logger.debug("In dancing.");
		}
		
		@Loggable4Event
		public void runForwards(boolean hasStickInMouth, boolean throwException) {
			logger.debug("Inside run forward method.");
			
			Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = true;
			runBackwards();
			
			if (throwException) {
				throw new RuntimeException("Throwing exception as planned.");
			}
			
			wagTail();
		}
		
		@Loggable4Event
		private void wagTail() {
			logger.debug("Inside wag tail method.");
			
			Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = true;
			unwagTail();
		}
		
		@Loggable4Event
		private void unwagTail() {
			logger.debug("Inside unwagTail tail method.");
			
			Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = true;
			wagTail();
		}
		
		@Loggable4Event
		public void runBackwards() {
			logger.debug("Inside dog run backwards method.");
			
			Log4EventAspect.isPreventNextLineFromExecutingAndAddToUndoLog = true;
			runForwards(true, false);
		}
	}
}

