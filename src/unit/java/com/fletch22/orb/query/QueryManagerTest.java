package com.fletch22.orb.query;

import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.test.data.TestDataSimple;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class QueryManagerTest {
	
	Logger logger = LoggerFactory.getLogger(QueryManagerTest.class);
	
	@Autowired
	CriteriaFactory criteriaFactory;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	TestDataSimple testDataSimple;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	QueryManagerImpl queryManager;
	
	@Before
	public void before() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	@After
	public void after() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}

	@Test
	public void testSaveQuery() {
		
		// Arrange
		StopWatch stopWatch = new StopWatch();
		long orbTypeInternalId = testDataSimple.loadTestData();
		
		stopWatch.start();
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		Criteria criteria = criteriaFactory.getInstance(orbType);
		
		// Act
		long orbInternalId = queryManager.create(criteria);
		stopWatch.stop();
		
		BigDecimal millis = new BigDecimal(stopWatch.getNanoTime()).divide(new BigDecimal(1000000));
		logger.info("Elapsed time: {}", millis);
		
		// Assert
		assertFalse(orbInternalId == Orb.INTERNAL_ID_UNSET);
	}

}
