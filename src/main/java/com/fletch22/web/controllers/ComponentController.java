package com.fletch22.web.controllers;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fletch22.app.designer.ComponentFactory;
import com.fletch22.app.designer.ComponentSaveFromMapService;
import com.fletch22.app.designer.viewmodel.AllModels;
import com.fletch22.app.state.FrontEndStateService;
import com.fletch22.app.state.StateIndexInfo;
import com.fletch22.util.json.GsonFactory;

@RestController
@RequestMapping("/api/component")
public class ComponentController extends Controller {

	Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	ComponentFactory componentFactory;

	@Autowired
	GsonFactory gsonFactory;

	@Autowired
	ComponentSaveFromMapService componentServiceRouter;

	@Autowired
	FrontEndStateService frontEndStateService;

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

	@RequestMapping(value = "/state", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String state(@RequestBody StatePackage statePackage) {

		String message = "Items saved: " + statePackage.states.size();
		frontEndStateService.save(statePackage.states);

		logger.info(message);

		return "{ \"result\": \"Success\" }";
	}

	@RequestMapping(value = "/stateHistory/{index}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody StateIndexInfo stateHistory(@PathVariable int index) {

		StateIndexInfo stateIndexInfo = frontEndStateService.getHistorical(index);
		logger.info("Getting {} state : {}: isEarliest: {}", index, stateIndexInfo.state, stateIndexInfo.isEarliestState);

		return stateIndexInfo;

	}

	public static class StatePackage {
		List<String> states;

		public void setStates(List<String> states) {
			this.states = states;
		}
	}
}
