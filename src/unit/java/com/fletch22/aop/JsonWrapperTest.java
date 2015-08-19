package com.fletch22.aop;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class JsonWrapperTest {
	
	Logger logger = LoggerFactory.getLogger(JsonWrapperTest.class);

	@SuppressWarnings("unused")
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
		
		testJsonWrapperPerf(jsonWrapper);
	}
	
	@Test
	public void testJsonWrapperLinkedHashSet() {
		
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		set.add("foo");
		set.add("bar");
		
		JsonWrapper jsonWrapper = new JsonWrapper(set);
		
		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(new Gson(), jsonWrapper.toJson());
		
		@SuppressWarnings("unchecked")
		LinkedHashSet<String> reconstituted = (LinkedHashSet<String>) jsonWrapper2.object;
		
		logger.info("jsonWrapper: {}", jsonWrapper.toJson());
		
		assertEquals("Should be 2 elements.", reconstituted.size(), 2);
	}
	
	@Test
	public void testLinkedHashmap() {

		LinkedHashMap<String, String> set = new LinkedHashMap<String, String>();
		set.put("fooKey", "fooValue");
		set.put("barKey", "barKey");
		
		JsonWrapper jsonWrapper = new JsonWrapper(set);
		
		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(new Gson(), jsonWrapper.toJson());
		
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> reconstituted = (LinkedHashMap<String, String>) jsonWrapper2.object;
		
		logger.info("jsonWrapper: {}", jsonWrapper.toJson());
		
		assertEquals("Should be 2 elements.", reconstituted.size(), 2);
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
		
		@SuppressWarnings("unused")
		long millis = stopWatch.getNanoTime() / 100000000;
		
//		logger.info("Elapsed millis per unit: " + millis);
	}
}
