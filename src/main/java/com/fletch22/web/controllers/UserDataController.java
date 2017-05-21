package com.fletch22.web.controllers;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.query.RichOrbResult;
import com.fletch22.util.json.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
@RequestMapping("/api/userData")
public class UserDataController extends Controller {

	Logger logger = LoggerFactory.getLogger(UserDataController.class);

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

	@RequestMapping(value = "/collections/{orbTypeInternalId}", method = RequestMethod.GET)
	public @ResponseBody RichOrbResult getComponent(@PathVariable long orbTypeInternalId) {
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		
		OrbResultSet orbResultSet = queryManager.findAll(orbTypeInternalId);
		
		logger.info(String.format("Found %s orbs", orbResultSet.orbList.size()));
		
		return new RichOrbResult(orbResultSet.orbList, orbType);
	}
	
	@RequestMapping(value = "/collections/", method = RequestMethod.POST)
	public @ResponseBody String persistOrb(@RequestBody String body) {
		Orb orbPersisted = null;
		
		logger.info(body);
		
		PersistOrbInfo persistOrbInfo = parseJsonPersistOrbInfo(body);
		
		if (persistOrbInfo.orbInternalId.isPresent()) {
			// Update
			logger.info("Updating...");
			orbPersisted = orbManager.getOrb(persistOrbInfo.orbInternalId.get());
			setOrbAttributes(persistOrbInfo, orbPersisted);
		} else {
			// Save
			logger.info("Saving orb...");
			orbPersisted = new Orb();
			orbPersisted.setOrbTypeInternalId(persistOrbInfo.orbTypeInternalId);
			
			setOrbAttributes(persistOrbInfo, orbPersisted);
			orbPersisted = orbManager.createOrb(orbPersisted);
		}
				
		return String.format("{ \"result\": \"Success\", \"orbInternalId\": %s }", orbPersisted.getOrbInternalId());
	}

	private void setOrbAttributes(PersistOrbInfo persistOrbInfo, Orb orb) {
		Map<String, String> attributeMap = persistOrbInfo.attributes;
		Set<String> attributeKeys = attributeMap.keySet();
		for (String key : attributeKeys) {
			OrbType orbType = orbTypeManager.getOrbType(persistOrbInfo.orbTypeInternalId);
			
			LinkedHashSet<String> customFields = orbType.customFields;
			if (!customFields.contains(key)) {
				throw new RuntimeException(String.format("When saving orb, did not recogize attribute %s.", key));
			}
			logger.info(String.format("Setting property %s: value: %s", key, attributeMap.get(key)));
			orb.getUserDefinedProperties().put(key, attributeMap.get(key));
		}
	}
	
	public PersistOrbInfo parseJsonPersistOrbInfo(String jsonPersistOrbInfo) {
		Gson gson = gsonFactory.getInstance();
		JsonObject jsonObject = gson.fromJson(jsonPersistOrbInfo, JsonObject.class);
		
		long collectionId = jsonObject.get("collectionId").getAsLong();

		PersistOrbInfo persistOrbInfo = new PersistOrbInfo();
		persistOrbInfo.orbTypeInternalId = collectionId;

		JsonObject row = jsonObject.getAsJsonObject("row");
		String strId = row.get("id").getAsString();
		if (tryParseLong(strId)) {
			long id = Long.parseLong(strId);
			persistOrbInfo.orbInternalId = Optional.of(id);
		}
		
		JsonObject joAttributes = row.getAsJsonObject("attributes");
		Set<Map.Entry<String, JsonElement>> keyValues = joAttributes.entrySet();
		for (Map.Entry<String, JsonElement> keyVal : keyValues) {
			String property = keyVal.getKey();
			String value = keyVal.getValue().getAsString();
			persistOrbInfo.attributes.put(property, value);
		}
		
		return persistOrbInfo;
	}
	
	public static class PersistOrbInfo {
		long orbTypeInternalId;
		Optional<Long> orbInternalId = Optional.empty();
		Map<String, String> attributes = new HashMap<>();
	}
	
	boolean tryParseLong(String value) {
		boolean result = false;
	     try {  
	         Long.parseLong(value);  
	         result = true;  
	     } catch (NumberFormatException e) {  
	         // Do nothing  
	     }
	     return result;
	}
}
