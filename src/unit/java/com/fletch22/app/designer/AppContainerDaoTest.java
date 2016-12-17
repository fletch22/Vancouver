package com.fletch22.app.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import groovy.util.logging.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.cache.indexcollection.IndexedCollectionFactory;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.orb.query.constraint.ConstraintGrinder;
import com.fletch22.orb.query.constraint.CriteriaBuilder;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.util.StopWatch;
import com.googlecode.cqengine.IndexedCollection;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class AppContainerDaoTest {

	static Logger logger = LoggerFactory.getLogger(AppContainerDaoTest.class);

	@Autowired
	AppDesignerModule appDesignerInitialization;

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;

	@Autowired
	AppDesignerModule appDesignerModule;

	@Autowired
	AppContainerService appContainerService;

	@Autowired
	AppService appService;

	@Autowired
	DefLimitationManager defLimitationManager;

	@Autowired
	Cache cache;

	@Autowired
	IndexedCollectionFactory indexedCollectionFactory;
	
	@Autowired
	OrbManager orbManager;

	@Before
	public void before() {
		integrationSystemInitializer.addOrbSystemModule(appDesignerModule);
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}

	@After
	public void after() {
		integrationSystemInitializer.removeOrbSystemModules();
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}

	@Test
	public void testResolveDescendents() {

		// Arrange
		// Act
		StopWatch stopWatch = new StopWatch();

		AppContainer appContainer = appContainerService.createInstance("foo");

		App app = appService.createInstance("funnyBusiness");

		appContainerService.addToParent(appContainer, app);

		stopWatch.start();
		appContainer = appContainerService.get(appContainer.getId());
		appContainerService.clearAndResolveAllDescendents(appContainer);
		stopWatch.stop();

		stopWatch.logElapsed();

		// Assert
		assertNotNull(appContainer);
		assertEquals(1, appContainer.getChildren().getList().size());
	}

	@Test
	public void testUniqueConstraintOnLabelSuccess() {

		// Arrange
		// Act
		StopWatch stopWatch = new StopWatch();
	
		AppContainer appContainer = appContainerService.createInstance("foo");
		appContainerService.createInstance("foo2");
		appContainerService.createInstance("foo3");

		long orbTypeInternalId = appContainer.getOrbOriginal().getOrbTypeInternalId();
		
		String[] attributeNames = { AppContainer.ATTR_LABEL };
		Criteria criteria = new CriteriaBuilder(orbTypeInternalId).addNotAmongstUniqueConstraint(orbTypeInternalId, attributeNames).build();
		
		IndexedCollection<CacheEntry> indexedCollection = indexedCollectionFactory.createInstance();
		
		LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
		lhm.put(AppContainer.ATTR_PARENT, "fooZZZ");
		lhm.put(AppContainer.ATTR_CHILDREN, "fooTTT");
		lhm.put(AppContainer.ATTR_LABEL, "fooShizzle");
		
		Orb orb = new Orb(213, orbTypeInternalId, lhm);
		
		CacheEntry cacheEntry = new CacheEntry(orb);
		indexedCollection.add(cacheEntry);
		
		ConstraintGrinder constraintGrinder = new ConstraintGrinder(criteria, indexedCollection);

		List<CacheEntry> cacheEntryList = constraintGrinder.listCacheEntries();
		
		logger.info("orbResultSet: {}", cacheEntryList.size());
		
		assertEquals("Should have returned 1 result indicating not amongst unique labels. In other words, this synthetic orb would be unique if it was inserted.", cacheEntryList.size(), 1);
	}
	
//	@Test
//	public void testUniqueConstraintWithWhereClauseSuccess() {
//
//		// Arrange
//		// Act
//		StopWatch stopWatch = new StopWatch();
//
//		AppContainer appContainer = appContainerService.createInstance("foo1");
//		appContainer = appContainerService.createInstance("foo2");
//		
//		String parentIdFoo2 = String.valueOf(appContainer.getId());
//		logger.info("Id: {}; ParentId: {}", parentIdFoo2, appContainer.getParentId());
//		
//		appContainerService.createInstance("foo3");
//
//		long orbTypeInternalId = appContainer.getOrbOriginal().getOrbTypeInternalId();
//		
//		Criteria criteria = new CriteriaBuilder(orbTypeInternalId)
//		.addNotAmongstUniqueConstraint(orbTypeInternalId, AppContainer.ATTR_LABEL)
//		.addNotAmongstUniqueConstraint(orbTypeInternalId, AppContainer.ATTR_PARENT)
//		.build();
//				
//		IndexedCollection<CacheEntry> indexedCollection = indexedCollectionFactory.createInstance();
//		
//		LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
//		lhm.put(AppContainer.ATTR_PARENT, parentIdFoo2);
//		lhm.put(AppContainer.ATTR_CHILDREN, "fooTTT");
//		lhm.put(AppContainer.ATTR_LABEL, "foo2");
//		
//		Orb orb = new Orb(213, orbTypeInternalId, lhm);
//		
//		CacheEntry cacheEntry = new CacheEntry(orb);
//		indexedCollection.add(cacheEntry);
//		
//		logger.info("About to grind results.");
//		ConstraintGrinder constraintGrinder = new ConstraintGrinder(criteria, indexedCollection);
//
//		List<CacheEntry> cacheEntryList = constraintGrinder.listCacheEntries();
//		
//		logger.info("orbResultSet: {}", cacheEntryList.size());
//		assertEquals("Should have returned 1 result indicating not amongst unique labels. In other words, this synthetic orb would be unique if it was inserted.", 1, cacheEntryList.size());
//	}
	
	@Test
	public void testUniqueConstraintOnLabelFails() {

		// Arrange
		AppContainer appContainer = appContainerService.createInstance("foo");
		
		logger.info("appContainer otid: {}", appContainer.getOrbOriginal().getOrbTypeInternalId());

		boolean wasExceptionThrown = false;
		
		// Act
		try {
			appContainerService.createInstance("foo");
		} catch (Exception e) {
			wasExceptionThrown = true;
		}

		// Assert
		assertTrue(wasExceptionThrown);
	}
}

