package com.fletch22.orb.command;

import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;

@Component
public class ActionSniffer {
	
	private static final String PREFIX_TEMPLATE_1 = "{\"%s\":{\"";
	private static final String PREFIX_TEMPLATE_2 = "{\"%s\":\"";
	private static final String PREFIX_TEMPLATE_3 = "{\"%s\":[{\"";
	private static final String ROOT_PREFIX_1 = String.format(PREFIX_TEMPLATE_1, CommandExpressor.ROOT_LABEL);
	private static final String ROOT_PREFIX_2 = String.format(PREFIX_TEMPLATE_2, CommandExpressor.ROOT_LABEL);
	private static final String SYSTEM_COMMAND_PREFIX_1 = String.format(PREFIX_TEMPLATE_1, CommandExpressor.SYSTEM_COMMAND);
	private static final String SYSTEM_COMMAND_PREFIX_2 = String.format(PREFIX_TEMPLATE_2, CommandExpressor.SYSTEM_COMMAND);
	
	private static final String LOG_BUNDLE_PREFIX = String.format(PREFIX_TEMPLATE_3, CommandExpressor.LOG_BUNDLE);

	public String getVerb(StringBuilder action) {
		String verb = null;
		
		String prefix = getPrefixInUse(action);
		
		if (prefix.equals(LOG_BUNDLE_PREFIX)) {
			verb = CommandExpressor.LOG_BUNDLE; 
		} else {
			verb = getVerbWithPrefix(prefix, action);
		}
		
		
		if (null == verb) {
			throw new RuntimeException("Encountered problem attempting to determine the type of action from string \"" + action + "\".");
		}
		
		return verb;
	}

	private String getPrefixInUse(StringBuilder action) {
		String prefixInUse = null;
		String actionRaw = action.toString();
		if (actionRaw.startsWith(ROOT_PREFIX_1)) {
			prefixInUse = ROOT_PREFIX_1;
		} else if (actionRaw.startsWith(ROOT_PREFIX_2)) {
			prefixInUse = ROOT_PREFIX_2;
		} else if (actionRaw.startsWith(SYSTEM_COMMAND_PREFIX_1)) {
			prefixInUse = SYSTEM_COMMAND_PREFIX_1;
		} else if (actionRaw.startsWith(SYSTEM_COMMAND_PREFIX_2)) {
			prefixInUse = SYSTEM_COMMAND_PREFIX_2;
		} else if (actionRaw.startsWith(LOG_BUNDLE_PREFIX)) {
			prefixInUse = LOG_BUNDLE_PREFIX;
		}
		
		if (null == prefixInUse) {
			throw new RuntimeException("Could not identify prefix when determining action type for action: " + actionRaw);
		}
		
		return prefixInUse;
	}

	private String getVerbWithPrefix(String prefix, StringBuilder action) {
		int prefixLength = prefix.length();
		int nextTickPos = action.indexOf("\"", prefixLength);
		return action.substring(prefixLength, nextTickPos);
	}
}
