package com.fletch22.app.designer.userData;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.AppDesignerModule
import com.fletch22.app.designer.Child
import com.fletch22.app.designer.ComponentChildren
import com.fletch22.app.designer.dataField.DataField
import com.fletch22.app.designer.dataField.DataFieldService
import com.fletch22.app.designer.dataModel.DataModel
import com.fletch22.app.designer.dataModel.DataModelService
import com.fletch22.app.state.diff.service.DeleteService
import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbTypeManager

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class ModelToUserDataTranslatorSpec extends Specification {
	
	@Autowired
	ModelToUserDataTranslator modelToUserDataTranslator
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	IntegrationSystemInitializer initializer
	
	@Autowired
	AppDesignerModule appDesignerModule
	
	@Autowired
	DataFieldService dataFieldService
	
	@Autowired
	OrbManager orbManager
	
	@Autowired
	DeleteService deleteService
	
	@Autowired
	DataModelService dataModelService
	
	def setup() {
		initializer.addOrbSystemModule(appDesignerModule)
		initializer.nukePaveAndInitializeAllIntegratedSystems()
	}
	
	def cleanup() {
		initializer.removeOrbSystemModules()
		initializer.nukePaveAndInitializeAllIntegratedSystems()
	}

	@Test
	def 'testCreateUserData'() {
		
		given:
		DataModel dataModel = new DataModel()
		dataModel.setId(123)
		ComponentChildren componentChildren = dataModel.getChildren()
		
		DataField dataField = new DataField()
		dataField.setId(124)
		dataField.label = "column_1"
		componentChildren.addChild(dataField)
		
		def label = modelToUserDataTranslator.composeUserDataTypeLabel(dataModel)
		
		when:
		modelToUserDataTranslator.createUserData(dataModel)
		
		then:
		def orbType = orbTypeManager.getOrbType(label)
		orbType != null
		dataField.label == orbType.customFields.getAt(0)
	}
	
	@Test
	def 'testUpdateUserDataWithAdd'() {
		
		given:
		DataModel dataModel = new DataModel()
		dataModel.setId(123)
		ComponentChildren componentChildren = dataModel.getChildren()
		
		DataField dataField = new DataField()
		dataField.setId(124)
		dataField.label = "column_1"
		componentChildren.addChild(dataField)
		
		List<Child> labelList = dataModel.getChildren().getList()
		dataField = new DataField()
		dataField.setId(125)
		dataField.label = "column_2"
		componentChildren.addChild(dataField)
		
		def label = modelToUserDataTranslator.composeUserDataTypeLabel(dataModel)
		
		modelToUserDataTranslator.createUserData(dataModel)
		
		orbManager.deleteOrb(dataField.getId(), true)
		
		when:
		modelToUserDataTranslator.updateUserData(dataModel)
		
		then:
		def orbType = orbTypeManager.getOrbType(label)
		orbType != null
		orbType.customFields.size() == 2
		dataField.label == orbType.customFields.getAt(1)
	}
	
	@Test
	def 'testUpdateUserDataWithDelete'() {
		
		given:
		DataModel dataModel = new DataModel()
		dataModel.setId(123)
		ComponentChildren componentChildren = dataModel.getChildren()
		
		DataField dataField = new DataField()
		dataField.setId(124)
		dataField.label = "column_1"
		componentChildren.addChild(dataField)
		
		dataField = new DataField()
		dataField.setId(125)
		dataField.label = "column_2"
		componentChildren.addChild(dataField)
		
		def label = modelToUserDataTranslator.composeUserDataTypeLabel(dataModel)
		
		modelToUserDataTranslator.createUserData(dataModel)
		
//		List<Child> labelList = dataModel.getChildren().getList()
//		deleteComponentService.delete(dataField.getId())
		
		componentChildren.getList().remove(1);
		
//		dataModel = dataModelService.get(dataModel.getId())
		
		when:
		modelToUserDataTranslator.updateUserData(dataModel)
		
		then:
		def orbType = orbTypeManager.getOrbType(label)
		orbType != null
		orbType.customFields.size() == 1
		"column_1" == orbType.customFields.getAt(0)
	}

}
