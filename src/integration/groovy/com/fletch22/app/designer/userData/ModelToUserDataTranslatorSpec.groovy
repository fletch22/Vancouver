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
import com.fletch22.app.state.diff.service.AddChildService
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
	DataModelService dataModelService

	@Autowired
	DataFieldService dataFieldService

	@Autowired
	OrbManager orbManager

	@Autowired
	DeleteService deleteService

	@Autowired
	AddChildService addChildService

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
		componentChildren.addChildAtOrdinal(dataField, Child.ORDINAL_LAST)

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
		DataModel dataModel = dataModelService.createInstance('dmLabel1')

		DataField dataField1 = dataFieldService.createInstance('dfLabel1')
		dataModelService.addToParent(dataModel, dataField1)

		DataField dataField2 = dataFieldService.createInstance('dfLabel2')
		def idOriginalDataField2 = dataField2.getId()
		dataModelService.addToParent(dataModel, dataField2)

		def label = modelToUserDataTranslator.composeUserDataTypeLabel(dataModel)

		def  expected_label = 'newLabel'
		dataField2.label = expected_label
		dataFieldService.save(dataField2)

		when:
		dataModel = dataModelService.get(dataModel.id)
		dataModelService.clearAndResolveAllDescendents(dataModel)
		List<Child> children = dataModel.children.getList()
		
		DataField dataFieldFound = children.stream().filter {child -> ((Child) child).id == idOriginalDataField2 }.findFirst().get()
				
		then:
		dataFieldFound != null
		dataFieldFound.label == expected_label
	}

	@Test
	def 'testUpdateUserDataWithDelete'() {

		given:
		DataModel dataModel = dataModelService.createInstance('dmLabel1')
		
		DataField dataField1 = dataFieldService.createInstance('dfLabel1')
		dataModelService.addToParent(dataModel, dataField1)

		DataField dataField2 = dataFieldService.createInstance('dfLabel2')
		def idOriginalDataField2 = dataField2.getId()
		dataModelService.addToParent(dataModel, dataField2)

		dataModel.getChildren().getList().remove()

		when:
		modelToUserDataTranslator.updateUserData(dataModel)

		then:
		def orbType = orbTypeManager.getOrbType(label)
		orbType != null
		orbType.customFields.size() == 1
		"column_1" == orbType.customFields.getAt(0)
	}
}
