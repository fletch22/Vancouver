package com.fletch22.app.designer;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerDao;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class AppContainerDaoTest {
	
	@Autowired
	AppContainerDao appContainerDao;
	
	@Autowired
	AppDesignerModule appDesignerInitialization;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	AppDesignerModule appDesignerModule;
	
	@Autowired
	AppContainerService appContainerService;
	
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
	public void test() {
		
		// Arrange
		// Act
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		AppContainer appContainer = appContainerService.createInstance("foo");
		stopWatch.stop();
		
		stopWatch.logElapsed();
		
		// Assert
		assertNotNull(appContainer);
	}
}
