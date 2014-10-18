package com.fletch22.orb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaleighTest {
	
	Logger logger = LoggerFactory.getLogger(RaleighTest.class);

	@Test
	public void integrationTest() {
		logger.info("This is the integration test running.");
		
		assertTrue("This test should pass.", true);
	}
}
