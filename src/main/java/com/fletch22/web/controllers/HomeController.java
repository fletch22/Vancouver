package com.fletch22.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.app.designer.appContainer.AppContainerService;

@Controller
public class HomeController {
	
	Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	AppContainerService appContainerService;
	
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public @ResponseBody Object root() {

		logger.debug("Root hit!");
		
		return appContainerService.findByLabel(AppDesignerModule.DEFAULT_APP_CONTAINER_NAME);
	}
}
