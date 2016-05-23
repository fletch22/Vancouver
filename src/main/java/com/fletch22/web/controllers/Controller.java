package com.fletch22.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fletch22.util.json.GsonFactory;
import com.fletch22.web.controllers.ComponentController.ExceptionJSONInfo;

public class Controller {
	
	Logger logger = LoggerFactory.getLogger(Controller.class);
	
	public static final String JSON_SUCCESS = "{ \"result\": \"Success\" }";

	/*@ExceptionHandler(Exception.class)
	public void handleApplicationExceptions(Throwable exception, HttpServletResponse response) {
		throw new RestException(exception, ErrorCode.UKNOWN_ERROR);
	}*/
	
	@Autowired
	GsonFactory gsonFactory;
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody String handleBadRequest(HttpServletRequest req, Exception exception) {
		
		ExceptionJSONInfo info = new ExceptionJSONInfo();
	    info.url = req.getRequestURL().toString();
	    info.systemMessage = exception.getMessage();
	    info.errorCode = ErrorCode.UKNOWN_ERROR.getCode();
		
	    return gsonFactory.getInstance().toJson(info);
	} 
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RestException.class)
	@ResponseBody String handleBadRequest(HttpServletRequest req, RestException restException) {
		
		ExceptionJSONInfo info = new ExceptionJSONInfo();
	    info.url = req.getRequestURL().toString();
	    info.systemMessage = restException.getMessage();
	    info.errorCode = restException.errorCode.getCode();
		
	    return gsonFactory.getInstance().toJson(info);
	} 

	public enum ErrorCode {
		SAVE_STATE_FAILED("The save state operation failed.", 1),
		UKNOWN_ERROR("The operation failed for an as yet unknown reason.", 2),
		COULD_NOT_DETERMINE_GOOD_STATE_FROM_CLIENT_IDS("The system could not determine a good state from the client IDs", 3);
		
		private String message;
		private int code;
		
		private ErrorCode(String message, int code) {
			this.message = message;
			this.code = code;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public int getCode() {
			return this.code;
		}
	}
}
