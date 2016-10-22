package com.fletch22.web.controllers.exception;


@SuppressWarnings("serial")
public class RestException extends RuntimeException {
	public ErrorCode errorCode;
	public Throwable innerCause;
	
	public RestException(Throwable throwable, ErrorCode errorCode) {
		super(errorCode.getMessage(), throwable);
		this.errorCode = errorCode;
	}
	
	public RestException(ErrorCode errorCode) {
		this.innerCause = new RuntimeException(String.format("Encountered problem: %s", errorCode.getMessage()));
		this.errorCode = errorCode;
	}
	
	@Override
	public String toString() {
		return "RestException [errorCode=" + errorCode + ", innerCause=" + innerCause + "]";
	}
}
