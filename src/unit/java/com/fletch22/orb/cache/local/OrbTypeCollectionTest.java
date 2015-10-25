package com.fletch22.orb.cache.local;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.orb.OrbType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class OrbTypeCollectionTest {
	
	Logger logger = LoggerFactory.getLogger(OrbTypeCollectionTest.class);
	
	OrbTypeCollection orbTypeCollection;
	
	@Autowired
	Cache cache;
	
	@Before
	public void before() {
		orbTypeCollection = cache.orbTypeCollection;
	}

	@Test
	public void testReadByIdSpeed() {
		
		BigDecimal tranDate = new BigDecimal("23412433124.012431234");
		
		int max = 100000;
		StopWatch stopWatch = new StopWatch();
		
		Map<Long, OrbType> entries = new HashMap<Long, OrbType>();
		for (long i = 0; i < max; i++) {
			LinkedHashSet<String> fields = getStubbedFields();
			OrbType orbType = new OrbType(i, RandomStringUtils.randomAlphabetic(7), tranDate, fields);
			entries.put(orbType.id, orbType);
		}

		stopWatch.start();
		entries.get(56);
		stopWatch.stop();
		
		double seconds = (double)stopWatch.getNanoTime() / 1000000000.0;
		logger.debug("Map Time: {}", seconds);
		
		orbTypeCollection.deleteAll();
		
		for (long i = 0; i < max; i++) {
			LinkedHashSet<String> fields = getStubbedFields();
			String jack = RandomStringUtils.randomAlphabetic(50) + i;
			OrbType orbType = new OrbType(i, jack, tranDate, fields);
			orbTypeCollection.add(orbType);
		}
		
		stopWatch = new StopWatch();
		stopWatch.start();
		orbTypeCollection.get(999);
		stopWatch.stop();
		
		seconds = (double)stopWatch.getNanoTime() / 1000000000.0;
		logger.debug("CG Time: {}", seconds);
		logger.debug("Nano time: {}", stopWatch.getNanoTime());
		assertTrue(stopWatch.getNanoTime() < 45000); 
	}

	private LinkedHashSet<String> getStubbedFields() {
		LinkedHashSet<String> fields = new LinkedHashSet<String>();
		fields.add("columnName1");
		fields.add("columnName2");
		fields.add("columnName3");
		return fields;
	}

}

