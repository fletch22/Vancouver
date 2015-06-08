package com.fletch22.aop;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class AopTest {
	
	Logger logger = LoggerFactory.getLogger(AopTest.class);
	
	@Autowired
	@Qualifier("fooNormal")
	Foo foo;

	@Test
	public void test() {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		foo.bar();
		stopWatch.stop();
		
		logger.info("Ntime: {}", stopWatch.getNanoTime());
		
		stopWatch = new StopWatch();
		stopWatch.start();
		foo.bar2();
		stopWatch.stop();

		logger.info("Ntime: {}", stopWatch.getNanoTime());
		
		stopWatch = new StopWatch();
		stopWatch.start();
		foo.bar();
		stopWatch.stop();
		
		logger.info("Ntime: {}", stopWatch.getNanoTime());
		
		assertTrue(1 == 1);
	}
	
	@Test
	public void testLog4EventSourcing() {
		
		// Arrange
		// Act
//		foo.bar3();
		
		foo.bar4("bananaValue", 4L);
		
		// Assert
		// Do nothing
	}
	
}
