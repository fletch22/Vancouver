package com.fletch22.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fletch22.app.designer.ComponentFactory;
import com.fletch22.app.designer.ComponentSaveFromMapService;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.dataModel.DataModel;
import com.fletch22.app.designer.dataModel.DataModelService;
import com.fletch22.app.designer.userData.ModelToUserDataTranslator;
import com.fletch22.app.state.FrontEndStateService;
import com.fletch22.app.state.diff.service.DeleteService;
import com.fletch22.dao.LogBackupAndRestore;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.util.json.GsonFactory;

@RestController
@RequestMapping("/api/dataModels")
public class DataModelController extends Controller {

	Logger logger = LoggerFactory.getLogger(DataModelController.class);

	@Autowired
	ComponentFactory componentFactory;

	@Autowired
	GsonFactory gsonFactory;

	@Autowired
	ComponentSaveFromMapService componentServiceRouter;

	@Autowired
	FrontEndStateService frontEndStateService;

	@Autowired
	DeleteService deleteService;

	@Autowired
	AppContainerService appContainerService;

	@Autowired
	QueryManager queryManager;

	@Autowired
	OrbManager orbManager;

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;

	@Autowired
	LogBackupAndRestore logBackupAndRestore;

	@Autowired
	ModelToUserDataTranslator modelToUserDataTranslator;

	@Autowired
	DataModelService dataModelService;

	@RequestMapping(value = "/{dataModelId}/lookupCollectionId/", method = RequestMethod.POST)
	public @ResponseBody String getComponent(@PathVariable long dataModelId) {
		DataModel dataModel = dataModelService.get(dataModelId);

		OrbType orbType = modelToUserDataTranslator.getUserDataType(dataModel);

		return String.format("{ \"collectionId\": \"%s\" }", orbType.id);
	}
}
