package com.fletch22.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.util.json.GsonFactory;

public class Controller {
	
	Logger logger = LoggerFactory.getLogger(Controller.class);
	
	public static final String JSON_SUCCESS = "{ \"result\": \"Success\" }";

	@Autowired
	GsonFactory gsonFactory;
}
