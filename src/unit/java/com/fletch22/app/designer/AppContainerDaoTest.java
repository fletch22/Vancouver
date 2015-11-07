package com.fletch22.app.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.util.StopWatch;

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
		
		logger.info("ID: {}", app.getId());
		logger.info("AppContainer ID: {}", appContainer.getId());
		
		appContainerService.addToParent(appContainer, app);
		
		stopWatch.start();
		appContainer = appContainerService.get(appContainer.getId());
		appContainerService.resolveAllDescendents(appContainer);
		stopWatch.stop();
		
		stopWatch.logElapsed();
		
		// Assert
		assertNotNull(appContainer);
		assertEquals(1, appContainer.getChildren().list().size());
	}
}
