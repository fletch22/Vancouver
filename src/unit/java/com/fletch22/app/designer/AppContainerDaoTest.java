package com.fletch22.app.designer;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.util.StopWatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class AppContainerDaoTest {
	
	@Autowired
	AppContainerDao appContainerDao;
	
	@Autowired
	AppDesignerInitialization appDesignerInitialization;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Before
	public void before() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
		integrationSystemInitializer.initializeSystem();
		appDesignerInitialization.initialize();
	}
	
	@After
	public void after() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}

	@Test
	public void test() {
		
		// Arrange
		// Act
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		AppContainer appContainer = appContainerDao.create("foo");
		stopWatch.stop();
		
//		stopWatch.logElapsed();
		
		// Assert
		assertNotNull(appContainer);
	}

}
