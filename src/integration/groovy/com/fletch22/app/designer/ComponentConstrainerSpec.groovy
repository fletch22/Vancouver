package com.fletch22.app.designer;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.appContainer.AppContainer
import com.fletch22.app.designer.appContainer.AppContainerService
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class ComponentConstrainerSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(ComponentConstrainerSpec)
	
	@Autowired
	ComponentConstrainer componentConstrainer
	
	@Autowired
	AppDesignerModule appDesignerInitialization;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	AppDesignerModule appDesignerModule;
	
	@Autowired
	AppContainerService appContainerService;
	
	def setup() {
		integrationSystemInitializer.addOrbSystemModule(appDesignerModule);
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	def teardown() {
		integrationSystemInitializer.removeOrbSystemModules();
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}
}
