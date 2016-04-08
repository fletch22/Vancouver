package com.fletch22.web.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.app.designer.ComponentFactory;
import com.fletch22.app.designer.ComponentSaveFromMapService;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.viewmodel.AllModels;
import com.fletch22.app.state.FrontEndStateService;
import com.fletch22.util.json.GsonFactory;

@RestController
@RequestMapping("/api")
public class AppDesignerController {

	Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	AppContainerService appContainerService;

	@Autowired
	AppService appService;

	@Autowired
	ComponentFactory componentFactory;

	@Autowired
	GsonFactory gsonFactory;
	
	@Autowired
	TransformerDocks transformerDocks;
	
	@Autowired
	ComponentSaveFromMapService componentServiceRouter;

	@Autowired
	FrontEndStateService frontEndStateService;
	
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public @ResponseBody Object getRootAppContainer() {

		AppContainer appContainer = appContainerService
				.findByLabel(AppDesignerModule.DEFAULT_APP_CONTAINER_NAME);

		appContainerService.clearAndResolveAllDescendents(appContainer);

		return appContainer;
	}

	@RequestMapping(value = "/components/{id}", method = RequestMethod.GET)
	public @ResponseBody Object getComponent(@PathVariable long id) {
		return componentFactory.getInstance(id);
	}

	@RequestMapping(value = "/components", method = RequestMethod.POST, consumes={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Object addComponent(@RequestBody Map<String, String> extParameters) {
		
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
	
	@RequestMapping(value = "/state", method = RequestMethod.PUT, consumes={MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String state(@RequestBody StatePackage statePackage) {
		
		String message = "Items saved: " + statePackage.states.size();
		frontEndStateService.save(statePackage.states);
		
		logger.info(message);
		
		return "{ \"result\": \"Success\" }";
	}
	
	@RequestMapping(value = "/stateMostRecent", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String stateMostRecent() {
		logger.info("Getting state most recent.");
		return "{ \"state\": " + frontEndStateService.getMostRecent() + "}";
		
	}
	
	public static class StatePackage {
		List<String> states;
		
		public void setStates(List<String> states) {
			this.states = states;
		}
	}

	@ExceptionHandler(RuntimeException.class)
	public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) {
	   try {
		   logger.info("An exception was thrown: {}", exception.getMessage());
		   exception.printStackTrace();
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
