package com.fletch22.web.controllers.exception;

public enum ErrorCode {
	SAVE_STATE_FAILED("The save state operation failed.", 1), 
	UKNOWN_ERROR("The operation failed for an as yet unknown reason.", 2), 
	COULD_NOT_DETERMINE_GOOD_STATE_FROM_CLIENT_IDS("The system could not determine a good state from the client IDs", 3), 
	CONSTRAINT_VIOLATION("The system refused to accept your update; your data has violated a data constraint.", 4), 
	NO_PREVIOUS_ERROR_STATES("The system could not find any previous states.", 5), 
	CLIENT_THINKS_TALKING_TO_PREV_INSTANCE_OLD_SERVER("The server probabl restarted. Client updates cannot be accepted until the client realizes this.", 6);

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