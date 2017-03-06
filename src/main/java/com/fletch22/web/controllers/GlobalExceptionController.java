package com.fletch22.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fletch22.orb.criteria.tester.F22ConstraintViolationException;
import com.fletch22.util.json.GsonFactory;
import com.fletch22.web.controllers.ComponentController.ExceptionJSONInfo;
import com.fletch22.web.controllers.exception.ErrorCode;
import com.fletch22.web.controllers.exception.RestException;

@ControllerAdvice
public class GlobalExceptionController {
	
	Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);
	
	@Autowired
	GsonFactory gsonFactory;

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public String handleBadRequest(HttpServletRequest req, Exception exception) {
		
		logger.info("Handling exception {}", exception);

		ExceptionJSONInfo info;
		info = new ExceptionJSONInfo();
		info.url = req.getRequestURL().toString();
		info.systemMessage = exception.getMessage();
		info.errorCode = ErrorCode.UKNOWN_ERROR.getCode();
		
		return gsonFactory.getInstance().toJson(info);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RestException.class)
	@ResponseBody
	ResponseEntity<String> handleBadRequest(HttpServletRequest req, RestException restException) {
		
		logger.info("Handling RestException");

		restException.printStackTrace();

		ExceptionJSONInfo info = getUsefulInformation(req.getRequestURL().toString(), restException);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new ResponseEntity<String>(gsonFactory.getInstance().toJson(info), headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ExceptionJSONInfo getUsefulInformation(String requestUrl, RestException restException) {
		
		ExceptionJSONInfo excpetionJSONInfo = new ExceptionJSONInfo();
		ExceptionInfo exceptionInfo = isOriginatedByConstraintViolation(restException);

		logger.error("Rest Exception.", restException);
		
		if (exceptionInfo.isConstraintException) {
			excpetionJSONInfo.url = requestUrl;
			excpetionJSONInfo.systemMessage = exceptionInfo.systemMessage;
			excpetionJSONInfo.errorCode = ErrorCode.CONSTRAINT_VIOLATION.getCode();
		} else {
			excpetionJSONInfo.url = requestUrl;
			excpetionJSONInfo.systemMessage = restException.errorCode.getMessage();
			excpetionJSONInfo.errorCode = restException.errorCode.getCode();
		}

		return excpetionJSONInfo;
	}

	private ExceptionInfo isOriginatedByConstraintViolation(Throwable throwable) {
		ExceptionInfo exceptionInfo = new ExceptionInfo();

		Throwable throwableCause = throwable.getCause();
		if (throwableCause != null) {
			logger.debug("Digging into exception, I found exception class '{}'.", throwableCause.getClass().getName());
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
}
