package app.designer.website;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groovy.util.logging.Slf4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.website.Website;
import com.fletch22.app.designer.website.WebsiteService;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.cache.indexcollection.IndexedCollectionFactory;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.util.StopWatch;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class WebsiteTest {

	static Logger logger = LoggerFactory.getLogger(WebsiteTest.class);

	@Autowired
	AppDesignerModule appDesignerInitialization;

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;

	@Autowired
	AppDesignerModule appDesignerModule;

	@Autowired
	AppService appService;

	@Autowired
	WebsiteService websiteService;

	@Autowired
	DefLimitationManager defLimitationManager;

	@Autowired
	Cache cache;

	@Autowired
	IndexedCollectionFactory indexedCollectionFactory;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	TransactionService transactionService;

	@Before
	public void before() {
		integrationSystemInitializer.addOrbSystemModule(appDesignerModule);
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	@After
	public void after() {
		integrationSystemInitializer.removeOrbSystemModules();
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	@Test
	public void testAddPerformance() {
		App app1 = appService.createInstance("app1");
		
		int totalCount = 10;
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (int i = 0; i < totalCount; i++)  {
			String labelExpected = "foo-" + String.valueOf(i);
			Website website1 = websiteService.createInstance(labelExpected);
			appService.addToParent(app1, website1);
		}
		stopWatch.stop();
		stopWatch.logElapsed("Many websites time elapsed: ");
	}
	
	@Test
	public void testSecondWebsiteToNewAppShouldSucceed() {
		logger.debug("Entering the race ...");
		
		App app1 = appService.createInstance("app1");
		
		logger.debug("About to create website ...");
		
		String labelExpected = "foo";
		Website website1 = websiteService.createInstance(labelExpected);
		logger.debug("created website: {}", website1.getId());
		
		appService.addToParent(app1, website1);
		
		logger.debug("Website ID: {}", website1.getId());
		
		App app2 = appService.createInstance("app2");
		Website website2 = websiteService.createInstance(labelExpected);
		
		boolean wasExceptionThrown = false;
		try {
			appService.addToParent(app2, website2);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		assertFalse(wasExceptionThrown);
	}
	
	@Test
	public void testHeadFakeSecondWebsiteToNewAppShouldSucceed() {
		logger.debug("Entering the race ...");
		
		App app1 = appService.createInstance("app1");
		
		logger.debug("About to create website ...");
		
		String labelExpected = "foo";
		Website website1 = websiteService.createInstance(labelExpected);
		appService.addToParent(app1, website1);
		
		Website website2 = websiteService.createInstance(labelExpected);
		
		boolean wasExceptionThrown = false;
		try {
			appService.addToParent(app1, website2);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		website2.label = "bar";
		websiteService.save(website2);
		
		assertTrue(wasExceptionThrown);
		
		Website website3 = websiteService.createInstance(labelExpected);
		
		App app2 = appService.createInstance("app2");
		wasExceptionThrown = false;
		try {
			appService.addToParent(app2, website3);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		assertFalse(wasExceptionThrown);
	}
	
	@Test
	public void testSetAttributeWithCheckConstraintThrowsSuccessfully() {
		logger.debug("Entering the race ...");
		
		App app1 = appService.createInstance("app1");
		
		String labelExpected = "foo";
		Website website1 = websiteService.createInstance(labelExpected);
		logger.debug("created website: {}", website1.getId());
		
		appService.addToParent(app1, website1);
		
		String labelOriginal = "bar";
		Website website2 = websiteService.createInstance(labelOriginal);
		appService.addToParent(app1, website2);
		
		boolean wasExceptionThrown = false;
		
		try {
			orbManager.setAttributeWithCheckConstraints(website2.getId(), Website.ATTR_LABEL, "foo");
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		Website websiteCopy = websiteService.get(website2.getId());
		
		assertTrue(wasExceptionThrown);
		logger.debug(websiteCopy.label);
		
		assertTrue(websiteCopy.label.equals(labelOriginal));
	}
	
	@Test
	public void testSecondWebsiteToAppShouldFail() {

		App app1 = appService.createInstance("app1");
		
		String labelExpected = "foo";
		Website website1 = websiteService.createInstance(labelExpected);
		appService.addToParent(app1, website1);
		
		logger.debug("Website ID: {}", website1.getId());
		
		Website website2 = websiteService.createInstance(labelExpected);
		
		boolean wasExceptionThrown = false;
		try {
			appService.addToParent(app1, website2);
		} catch (Exception e) {
			wasExceptionThrown = true;
		}
		
		assertTrue(wasExceptionThrown);
	}
}

