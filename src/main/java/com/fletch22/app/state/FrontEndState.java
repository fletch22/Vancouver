package com.fletch22.app.state;

import java.util.Arrays;
import java.util.LinkedHashSet;


public class FrontEndState {
	public static String TYPE_LABEL = "frontEndState";
	public static String ATTR_STATE = "state";
	public static String ATTR_CLIENT_ID = "clientId";
	public static String ATTR_ASSOCIATED_TRANSACTION_ID = "associatedTransactionId";
	public static String QUERY_GET_STATES = "findFrontEndStates";
	
	public static LinkedHashSet<String> ATTRIBUTE_LIST = new LinkedHashSet<String>(Arrays.asList(ATTR_STATE, ATTR_ASSOCIATED_TRANSACTION_ID, ATTR_CLIENT_ID)); 
}
