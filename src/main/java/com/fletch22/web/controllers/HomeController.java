package com.fletch22.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController extends Controller {
	
	Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(path = "/test", method = RequestMethod.GET)
	public @ResponseBody Object testEndpoint() {
		logger.info("Called test endpoint.");
		
		return "Called test endpoint.";
	}
}
