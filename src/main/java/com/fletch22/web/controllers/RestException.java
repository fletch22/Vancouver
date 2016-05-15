package com.fletch22.web.controllers;

import com.fletch22.web.controllers.Controller.ErrorCode;

@SuppressWarnings("serial")
public class RestException extends RuntimeException {
	public ErrorCode errorCode;
	public Throwable innerCause;
	
	public RestException(Throwable throwable, ErrorCode errorCode) {
		super(errorCode.getMessage(), throwable);
		this.errorCode = errorCode;
	}
}
