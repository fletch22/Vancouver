package com.fletch22.app.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class AppContainerDaoTest {
	
	@Autowired
	AppDesignerDao appContainerDao;
	
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
		integrationSystemInitializer.clearOrbSystemModules();
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
		appContainerService.resolveAllDescendents(appContainer);
		stopWatch.stop();
		
		stopWatch.logElapsed();
		
		// Assert
		assertNotNull(appContainer);
		assertEquals(1, appContainer.getChildren().list().size());
	}
}
