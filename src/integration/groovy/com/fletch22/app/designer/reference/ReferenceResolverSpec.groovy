package com.fletch22.app.designer.reference;

import static org.junit.Assert.*

import org.apache.commons.lang3.NotImplementedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.AppDesignerModule
import com.fletch22.app.designer.app.App
import com.fletch22.app.designer.app.AppService
import com.fletch22.app.designer.appContainer.AppContainer
import com.fletch22.app.designer.appContainer.AppContainerService
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class ReferenceResolverSpec extends Specification {
	
	@Autowired
	ReferenceResolverService referenceResolver
	
	@Autowired
	AppContainerService appContainerService
	
	@Autowired
	AppService appService
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	@Autowired
	AppDesignerModule appDesignerModule
	
	def setup() {
		initializer.addOrbSystemModule(appDesignerModule)
		initializer.nukePaveAndInitializeAllIntegratedSystems()
	}
	
	def cleanup() {
		initializer.removeOrbSystemModules()
		initializer.nukePaveAndInitializeAllIntegratedSystems()
	}
	
	def 'test resolve'() {
		
		given:
		when:
		AppContainer appContainer = appContainerService.createInstance("foo")
		
		App app = appService.createInstance("fooAppChild")
		appContainerService.addToParent(appContainer, app)
		
		then:
		appContainer.getChildren().isHaveChildrenBeenResolved()
	}
	
	def 'test add dupes'() {
		
		given:
		when:
		AppContainer appContainer = appContainerService.createInstance("foo")
		
		App app = appService.createInstance("fooAppChild")
		appContainerService.addToParent(appContainer, app)
		
		then:
		appContainer.getChildren().isHaveChildrenBeenResolved()
	}
}
