package com.fletch22.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;

@RestController
@RequestMapping("/api/appContainer")
public class AppDesignerController extends Controller {

	Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	AppContainerService appContainerService;

	@Autowired
	AppService appService;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public @ResponseBody Object getRootAppContainer() {

		AppContainer appContainer = appContainerService.getDefault();
		appContainerService.clearAndResolveAllDescendents(appContainer);

		return appContainer;
	}
}
