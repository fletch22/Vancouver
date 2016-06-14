package com.fletch22.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fletch22.app.designer.ComponentFactory;
import com.fletch22.app.designer.ComponentSaveFromMapService;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.service.DeleteComponentService;
import com.fletch22.app.designer.viewmodel.AllModels;
import com.fletch22.app.state.FrontEndState;
import com.fletch22.app.state.FrontEndStateDao.StateSearchResult;
import com.fletch22.app.state.FrontEndStateService;
import com.fletch22.app.state.StateIndexInfo;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.util.json.GsonFactory;
import com.fletch22.web.controllers.exception.RestException;

@RestController
@RequestMapping("/api/component")
public class ComponentController extends Controller {

	private static final String ACTION_ROLLBACK_TO = "rollbackTo";

	Logger logger = LoggerFactory.getLogger(ComponentController.class);

	@Autowired
	ComponentFactory componentFactory;

	@Autowired
	GsonFactory gsonFactory;

	@Autowired
	ComponentSaveFromMapService componentServiceRouter;

	@Autowired
	FrontEndStateService frontEndStateService;
	
	@Autowired
	DeleteComponentService baseComponentService;
	
	@Autowired
	AppContainerService appContainerService;
	
	@Autowired
	QueryManager queryManager;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody Object getComponent(@PathVariable long id) {
		return componentFactory.getInstance(id);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody Object addComponent(
			@RequestBody Map<String, String> extParameters) {

		logger.error(gsonFactory.getInstance().toJson(extParameters));

		Map<String, String> mapParam = extParameters;

		if (!mapParam.containsKey(AllModels.TYPE_LABEL)) {
			String message = String.format(
					"Data is missing some key info: specifically '%s.'",
					AllModels.TYPE_LABEL);
			throw new RuntimeException(message);
		}

		return componentServiceRouter.save(mapParam);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteComponent(@PathVariable long id) {

		baseComponentService.delete(id);

		return JSON_SUCCESS;
	}

	@RequestMapping(value = "/statePallet", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String saveStatePallet(@RequestBody StatePallet statePallet) {

		String message = "Items saved: " + statePallet.statePackages.size();
		
		logger.info("Size of state list: {}", queryManager.executeQuery(FrontEndState.QUERY_GET_STATES).orbList.size());
		
		if (statePallet.statePackages.get(0).state.contains("x")) {
			throw new RuntimeException("CONTAINS X");
		}
		
		frontEndStateService.save(statePallet.statePackages);

		logger.info(message);

		return JSON_SUCCESS;
	}
	
	@RequestMapping(value = "/statePackage", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String saveStatePackage(@RequestBody StatePackage statePackage) {
		
		String result = null; 
		try {
			result = frontEndStateService.saveStatePackage(statePackage);
		} catch (Exception e) {
			throw new RestException(e, ErrorCode.SAVE_STATE_FAILED);
		}

		return result;
	}

	@RequestMapping(value = "/stateHistory/{index}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody StateIndexInfo stateHistory(@PathVariable int index) {

		StateIndexInfo stateIndexInfo = frontEndStateService.getHistorical(index);
		logger.info("Getting {} state : {}: isEarliest: {}", index, stateIndexInfo.state, stateIndexInfo.isEarliestState);

		return stateIndexInfo;
	}
	
	@RequestMapping(value = "/mostRecentStateHistory", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody StateIndexInfo getMostRecentStateHistory() {

		StateIndexInfo stateIndexInfo = frontEndStateService.getMostRecentHistorical();

		return stateIndexInfo;
	}
	
	@RequestMapping(value = "/determineLastGoodState", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody LastGoodState determineLastGoodState(@RequestBody List<List<String>> idPackages) {
		
		logger.info("Size of clientIds: {}", idPackages.size());
		
		logger.info("Size of state list: {}", queryManager.executeQuery(FrontEndState.QUERY_GET_STATES).orbList.size());
		
		StateSearchResult searchResultState = frontEndStateService.determineLastGoodState(new ClientIdsPackage(idPackages));
		
		if (!searchResultState.isStateFound()) {
			throw new RestException(ErrorCode.COULD_NOT_DETERMINE_GOOD_STATE_FROM_CLIENT_IDS);
		}
		
		logger.info("Expecting to get a call to rollback to {}", searchResultState.clientId);

		return new LastGoodState(searchResultState.clientId, searchResultState.state);
	}
	
	public static class LastGoodState {
		public String clientId;
		public String stateJson;
		
		public LastGoodState(String clientId, String stateJson) {
			this.clientId = clientId;
			this.stateJson = stateJson;
		}
	}
	
	@RequestMapping(value = "/states/{stateClientId}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String rollbackToState(@PathVariable String stateClientId, @RequestParam(value="action", required=true) String action) {
		
		if (!action.equals(ACTION_ROLLBACK_TO)) {
			throw new RuntimeException(String.format("Unrecognized action '%s'. Must be '%s'.", action, ACTION_ROLLBACK_TO));
		}
		
		this.frontEndStateService.rollbackToState(stateClientId);
		
		return JSON_SUCCESS;
	}
	
	public static class ClientIdsPackage {
		public List<List<String>> idPackages;
		
		public ClientIdsPackage(List<List<String>> idPackages) {
			this.idPackages = idPackages;
		}
	}
	
	@RequestMapping(value = "/getExceptionForTesting", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody StateIndexInfo getExceptionForTesting() {
		throw new RestException(new Exception("test test test"), ErrorCode.UKNOWN_ERROR);
	}
	
	public static class ExceptionJSONInfo {
		public String url;
	    public String systemMessage;
	    public int errorCode;
	}
	
	public static class StatePallet { 
		ArrayList<StatePackage> statePackages;

		public void setStates(ArrayList<StatePackage> statePackages) {
			this.statePackages = statePackages;
		}
	}
	
	public static class StatePackage {
		public String state;
		public String diffBetweenOldAndNew;
		public String clientId;
	}
}