package com.fletch22.orb.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

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
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.client.service.BeginTransactionService;
import com.fletch22.orb.command.transaction.RollbackTransactionService;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaStandard;
import com.fletch22.orb.systemType.SystemType;
import com.fletch22.orb.test.data.TestDataSimple;
import com.fletch22.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class QueryManagerTest {
	
	Logger logger = LoggerFactory.getLogger(QueryManagerTest.class);
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	TestDataSimple testDataSimple;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	QueryManager queryManager;
	
	@Autowired
	BeginTransactionService beginTransactionService;
	
	@Autowired
	RollbackTransactionService rollbackTransactionService;
	
	@Autowired
	Cache cache;
	
	@Before
	public void before() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}
	
	@After
	public void after() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	@Test
	public void testSaveQuery() {
		
		// Arrange
		StopWatch stopWatch = new StopWatch();
		long orbTypeInternalId = testDataSimple.loadTestData();
		
		stopWatch.start();
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		Criteria criteria = new CriteriaStandard(orbType.id, "foo");
		
		// Act
		long orbInternalId = queryManager.addToCollection(criteria);
		stopWatch.stop();
		
		logger.debug("Elapsed time: {} millis.", stopWatch.getElapsedMillis());
		
		// Assert
		assertFalse(orbInternalId == Orb.INTERNAL_ID_UNSET);
	}
	
	@Test
	public void testAttemptSaveQueryWithDupeNames() {
		
		// Arrange
		long orbTypeInternalId = testDataSimple.loadTestData();
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		String dupeName = "bar";
		
		Criteria criteria1 = new CriteriaStandard(orbType.id, dupeName);
		queryManager.addToCollection(criteria1);
		
		Criteria criteria2 = new CriteriaStandard(orbType.id, dupeName);
		
		boolean wasExceptionThrown = false;
		try {
			queryManager.addToCollection(criteria2);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		// Act
		assertTrue(wasExceptionThrown);
	}
	
	@Test
	public void testRollbackQuery() throws Exception {
		
		// Arrange
		long orbTypeInternalId = testDataSimple.loadTestData();
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		assertEquals(0, cache.queryCollection.getSize());
		
		assertEquals(0, orbManager.countOrbsOfType(SystemType.CRITERIA.getId()));
		
		BigDecimal tranId = beginTransactionService.beginTransaction();
		
		Criteria criteria = new CriteriaStandard(orbType.id, "foo");
		
		queryManager.addToCollection(criteria);
		
		// Act
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId);
		
		// Assert
		assertEquals(0, cache.queryCollection.getSize());
		
		// There should be no orbs of type 'query'
		assertEquals(0, orbManager.countOrbsOfType(SystemType.CRITERIA.getId()));
	}
	
	@Test
	public void tesDeleteQueryWhenDeletingOrb() throws Exception {
		
		// Arrange
		long orbTypeInternalId = testDataSimple.loadTestData();
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		Criteria criteria = new CriteriaStandard(orbType.id, "foo");
		
		long orbInternalId = queryManager.addToCollection(criteria);
		
		// Act
		orbManager.deleteOrb(orbInternalId, true);
		
		// Assert
		assertEquals(0, cache.queryCollection.getSize());
		
		// There should be no orbs of type 'query'
		assertEquals(0, orbManager.countOrbsOfType(SystemType.CRITERIA.getId()));
	}

}
