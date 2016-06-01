package com.fletch22.orb.criteria.tester;

import com.fletch22.web.controllers.Controller.ErrorCode;

public class F22ConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 584598699801148073L;
	private String systemMessage;
	private ErrorCode errorCode = ErrorCode.CONSTRAINT_VIOLATION;
	
	public F22ConstraintViolationException(StringBuffer systemMessage) {
		this.systemMessage = systemMessage.toString();
	}

	public String getSystemMessage() {
		return this.systemMessage;
	}
	
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
}
