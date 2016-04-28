package com.fletch22.web.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class Controller {
	
	Logger logger = LoggerFactory.getLogger(Controller.class);
	
	public static final String JSON_SUCCESS = "{ \"result\": \"Success\" }";

	@ExceptionHandler(RuntimeException.class)
	public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) {
		try {
			logger.info("An exception was thrown: {}", exception.getMessage());
			exception.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					exception.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
