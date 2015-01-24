package com.fletch22.orb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.util.RandomUtil;

public class DataRetrievalStorageTest {
	
	Logger logger = LoggerFactory.getLogger(DataRetrievalStorageTest.class);
	
	Map<String, List<String>> map;

	@Before
	public void before() {
		
		this.map = new HashMap<String, List<String>>();
		
		int limit = 1000000;
		for (int i = 1; i < limit; i++) {
			this.map.put(String.valueOf(i), new ArrayList<String>());
		}
	}
	
	@Test
	public void testMap() {
	
		RandomUtil randomUtils = new RandomUtil();
		
		logger.info("Starting random number test.");
		for (int i = 0; i < 1000; i++) {
			int randomNumber = randomUtils.getRandom(1, 999999);
			this.map.get(String.valueOf(randomNumber));
		}
		
		logger.info("Starting random retrieval test.");
		for (int i = 0; i < 1000; i++) {
			int randomNumber = randomUtils.getRandom(1, 999999);
			this.map.get(String.valueOf(randomNumber));
		}
		
		logger.info("Ending random retrieval test.");
		
	}
}
