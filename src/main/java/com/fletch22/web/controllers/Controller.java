package com.fletch22.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fletch22.orb.criteria.tester.F22ConstraintViolationException;
import com.fletch22.util.json.GsonFactory;
import com.fletch22.web.controllers.ComponentController.ExceptionJSONInfo;
import com.fletch22.web.controllers.exception.RestException;

public class Controller {
	
	Logger logger = LoggerFactory.getLogger(Controller.class);
	
	public static final String JSON_SUCCESS = "{ \"result\": \"Success\" }";

	@Autowired
	GsonFactory gsonFactory;
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody String handleBadRequest(HttpServletRequest req, Exception exception) {
		
		exception.printStackTrace();
		
		ExceptionJSONInfo info = new ExceptionJSONInfo();
	    info.url = req.getRequestURL().toString();
	    info.systemMessage = exception.getMessage();
	    info.errorCode = ErrorCode.UKNOWN_ERROR.getCode();
		
	    return gsonFactory.getInstance().toJson(info);
	} 
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RestException.class)
	@ResponseBody String handleBadRequest(HttpServletRequest req, RestException restException) {
		
		restException.printStackTrace();
		
		ExceptionJSONInfo info = getUsefulInformation(req.getRequestURL().toString(), restException);
		
	    return gsonFactory.getInstance().toJson(info);
	} 

	private ExceptionJSONInfo getUsefulInformation(String requestUrl, RestException restException) {
		ExceptionJSONInfo excpetionJSONInfo = new ExceptionJSONInfo();
		ExceptionInfo exceptionInfo = isOriginatedByConstraintViolation(restException);
		
		if (exceptionInfo.isConstraintException) {
			excpetionJSONInfo.url = requestUrl;
		    excpetionJSONInfo.systemMessage = exceptionInfo.systemMessage;
		    excpetionJSONInfo.errorCode = ErrorCode.CONSTRAINT_VIOLATION.getCode();
		} else {
		    excpetionJSONInfo.url = requestUrl;
		    excpetionJSONInfo.systemMessage = restException.getMessage();
		    excpetionJSONInfo.errorCode = restException.errorCode.getCode();
		}
		
		return excpetionJSONInfo;
	}

	private ExceptionInfo isOriginatedByConstraintViolation(Throwable throwable) {
		ExceptionInfo exceptionInfo = new ExceptionInfo();
		
		Throwable throwableCause = throwable.getCause();
		logger.debug("Digging into exception, I found exception class '{}'.", throwableCause.getClass().getName());
		if (throwableCause != null) {
			if (throwableCause.getClass().getName().equals(F22ConstraintViolationException.class.getName())) {
				F22ConstraintViolationException f22ConstraintViolationException = (F22ConstraintViolationException) throwableCause;
				exceptionInfo.systemMessage = f22ConstraintViolationException.getSystemMessage();
				exceptionInfo.isConstraintException = true;
			} else {
				exceptionInfo = isOriginatedByConstraintViolation(throwableCause);
			}
		} 
		return exceptionInfo;
	}
	
	private class ExceptionInfo {
		public boolean isConstraintException = false;
		public String systemMessage;
	}
	
	public enum ErrorCode {
		SAVE_STATE_FAILED("The save state operation failed.", 1),
		UKNOWN_ERROR("The operation failed for an as yet unknown reason.", 2),
		COULD_NOT_DETERMINE_GOOD_STATE_FROM_CLIENT_IDS("The system could not determine a good state from the client IDs", 3),
		CONSTRAINT_VIOLATION("The system refused to accept your update; your data has violated a data constraint.", 4);
		
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
