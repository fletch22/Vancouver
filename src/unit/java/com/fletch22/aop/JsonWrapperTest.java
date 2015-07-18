package com.fletch22.aop;

import java.math.BigDecimal;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import com.google.gson.Gson;

public class JsonWrapperTest {
	
//	Logger logger = LoggerFactory.getLogger(JsonWrapperTest.class);

	@Test
	public void test() {
		
		// Arrange
		QueryThing queryTest = new QueryThing();
		StopWatch stopWatch = new StopWatch();
		
		int maxTimes = 100;
		
		stopWatch.start(); 
		for (int i = 0; i < maxTimes; i++) {
			JsonWrapper jsonWrapper = new JsonWrapper(queryTest);
			String thing = jsonWrapper.toJson();
		}
		stopWatch.stop();
		
		long millis = stopWatch.getNanoTime() / 100000000;
		
		//logger.info("Elapsed millis per unit: " + millis);
	}
	
	@Test
	public void testUnwrappingString()  {
		
		QueryThing queryTest = new QueryThing();
		
		JsonWrapper jsonWrapper = new JsonWrapper(queryTest);
		testJsonWrapperPerf(jsonWrapper);
		
		String stringThing = "funny";
		jsonWrapper = new JsonWrapper(stringThing);
		
		//logger.info(jsonWrapper.toJson());
		
		testJsonWrapperPerf(jsonWrapper);
	}
	
	@Test
	public void testUnwrappingBigDecimal()  {
		
		QueryThing queryTest = new QueryThing();
		
		JsonWrapper jsonWrapper = new JsonWrapper(queryTest);
		testJsonWrapperPerf(jsonWrapper);
		
		BigDecimal bigDecimal = new BigDecimal("1.200000123123213");
		jsonWrapper = new JsonWrapper(bigDecimal);
		
//		logger.info(jsonWrapper.toJson());
		
		testJsonWrapperPerf(jsonWrapper);
	}
	
	@Test
	public void testUnwrappingPrimitiveBoolean()  {
		
		QueryThing queryTest = new QueryThing();
		
		JsonWrapper jsonWrapper = new JsonWrapper(queryTest);
		testJsonWrapperPerf(jsonWrapper);
		
		boolean isDelicious = true;
		jsonWrapper = new JsonWrapper(isDelicious);
		
//		logger.info(jsonWrapper.toJson());
		
		testJsonWrapperPerf(jsonWrapper);
	}
	
	@Test
	public void testUnwrappingObjectBoolean()  {
		
		QueryThing queryTest = new QueryThing();
		
		JsonWrapper jsonWrapper = new JsonWrapper(queryTest);
		testJsonWrapperPerf(jsonWrapper);
		
		Boolean isDelicious = new Boolean(true);
		jsonWrapper = new JsonWrapper(isDelicious);
		
//		logger.info(jsonWrapper.toJson());
		
		testJsonWrapperPerf(jsonWrapper);
	}
	
	@Test
	public void testUnwrappingNullObjectBoolean()  {
		
		QueryThing queryTest = new QueryThing();
		
		JsonWrapper jsonWrapper = new JsonWrapper(queryTest);
		testJsonWrapperPerf(jsonWrapper);
		
		Boolean isDelicious = null;
		jsonWrapper = new JsonWrapper(isDelicious);
		
//		logger.info(jsonWrapper.toJson());
		
		testJsonWrapperPerf(jsonWrapper);
	}

	private void testJsonWrapperPerf(JsonWrapper jsonWrapper) {
		
		String json = jsonWrapper.toJson();
//		logger.info(json);
		
		Gson gson = new Gson();
		
		StopWatch stopWatch = new StopWatch();
		
		int maxTimes = 100;
		stopWatch.start(); 
		for (int i = 0; i < maxTimes; i++) {
			jsonWrapper = JsonWrapper.fromJson(gson, json);
//			logger.info("object: {}", jsonWrapper.object);
		}
		stopWatch.stop();
		
		long millis = stopWatch.getNanoTime() / 100000000;
		
//		logger.info("Elapsed millis per unit: " + millis);
	}
	
}
