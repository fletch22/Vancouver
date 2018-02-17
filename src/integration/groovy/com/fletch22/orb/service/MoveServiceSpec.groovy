package com.fletch22.orb.service;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.AppDesignerModule
import com.fletch22.app.designer.app.App
import com.fletch22.app.designer.app.AppService
import com.fletch22.app.designer.appContainer.AppContainer
import com.fletch22.app.designer.appContainer.AppContainerService
import com.fletch22.app.state.diff.service.MoveService
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.web.controllers.ComponentController.MoveCommand;
import com.fletch22.web.controllers.ComponentController.StatePackage;

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class MoveServiceSpec extends Specification {
	
	@Autowired
	MoveService moveService
	
	@Autowired
	AppService appService
	
	@Autowired
	AppContainerService appContainerService
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	AppDesignerModule appDesignerModule
	
	def setup() {
		integrationSystemInitializer.addOrbSystemModule(appDesignerModule)
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}
	
	def cleanup() {
		integrationSystemInitializer.removeOrbSystemModules()
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}
	
	def 'test move component'() {
		given:
		AppContainer appContainer1 = appContainerService.createInstance("foo1")
		AppContainer appContainer2 = appContainerService.createInstance("foo2")
		
		App app1 = appService.createInstance("fooAppChild1")
		appContainerService.addToParent(appContainer1, app1)
		
		appContainerService.clearAndResolveAllDescendents(appContainer1)
		appContainerService.clearAndResolveAllDescendents(appContainer2)
		
		assert appContainer1.getChildren().getList().size() == 1
		assert appContainer2.getChildren().getList().size() == 0
		
		MoveCommand moveCommand = new MoveCommand()
		moveCommand.statePackage = null
		moveCommand.sourceParentId = appContainer1.id
		moveCommand.destinationParentId = appContainer2.id
		moveCommand.childId = app1.id
		moveCommand.ordinalChildTarget = 0
		
		when:
		moveService.move(moveCommand)
		
		appContainer1 = appContainerService.get(appContainer1.id)
		appContainerService.clearAndResolveAllDescendents(appContainer1)
		
		appContainer2 = appContainerService.get(appContainer2.id)
		appContainerService.clearAndResolveAllDescendents(appContainer2)
		
		then:
		assert appContainer1.getChildren().getList().size() == 0
		assert appContainer2.getChildren().getList().size() == 1
	}

}
