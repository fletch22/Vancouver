package com.fletch22.web.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fletch22.web.controllers.ComponentController.ExceptionJSONInfo;

public class Controller {
	
	Logger logger = LoggerFactory.getLogger(Controller.class);
	
	public static final String JSON_SUCCESS = "{ \"result\": \"Success\" }";

//	@ExceptionHandler(RuntimeException.class)
//	public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) {
//		try {
//			logger.info("An exception was thrown: {}", exception.getMessage());
//			exception.printStackTrace();
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//					exception.getMessage());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@ExceptionHandler(RestException.class)
	public @ResponseBody ExceptionJSONInfo handleRestException(HttpServletRequest request, HttpServletResponse response, RestException restException){
	    
		logger.info("An exception was thrown: {}", restException.getMessage());
		restException.printStackTrace();

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
	    ExceptionJSONInfo info = new ExceptionJSONInfo();
	    info.url = request.getRequestURL().toString();
	    info.message = restException.getMessage();
	    info.errorCode = restException.errorCode.toString();
	     
	    return info;
	}
	
	public enum ErrorCode {
		SAVE_STATE_FAILED("The save state operation failed."),
		UKNOWN_ERROR("The operation failed for an as yet unknown reason.");
		
		private String message;
		
		private ErrorCode(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
}
