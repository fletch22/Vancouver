package com.fletch22.web.controllers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.app.designer.ComponentFactory;
import com.fletch22.app.designer.ComponentSaveFromMapService;
import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.viewmodel.AllModels;
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

	@ExceptionHandler(RuntimeException.class)
	public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) {
	   try {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
