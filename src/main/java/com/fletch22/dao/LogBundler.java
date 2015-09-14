package com.fletch22.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class LogBundler {

	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	public StringBuilder bundle(StringBuilder action, long internalIdBeforeOperation) {
		StringBuilder translation = new StringBuilder();

        translation.append("{\"" + CommandExpressor.LOG_BUNDLE + "\":[{\"");
        translation.append(CommandExpressor.ID_BEFORE_OPERATION);
        translation.append("\":\"");
        translation.append(internalIdBeforeOperation);
        translation.append("\"},");
        translation.append(action);
        translation.append("]}");

        return translation;
	}
	
	public LogBundleDto unbundle(StringBuilder bundledAction) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(bundledAction.toString());
		
		JsonArray logBundleArray = jsonObject.getAsJsonArray(CommandExpressor.LOG_BUNDLE);
				
		JsonElement idBeforeOperationElement = logBundleArray.get(0);
		JsonObject firstElementJsonObject = idBeforeOperationElement.getAsJsonObject();
		
		JsonPrimitive jsonPrimitiveFirst = firstElementJsonObject.getAsJsonPrimitive(CommandExpressor.ID_BEFORE_OPERATION);
		long idBeforeOperation = jsonPrimitiveFirst.getAsLong();
		
		JsonElement actionElement = logBundleArray.get(1);
		
		LogBundleDto logBundle = new LogBundleDto();
		logBundle.internalIdBeforeOperation = idBeforeOperation;
		logBundle.action = new StringBuilder(actionElement.toString());
		
		return logBundle;
	}
	
	public class LogBundleDto {
		public long internalIdBeforeOperation;
		public StringBuilder action;
	}
}
