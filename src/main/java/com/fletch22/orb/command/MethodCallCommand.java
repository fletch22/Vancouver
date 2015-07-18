package com.fletch22.orb.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fletch22.aop.JsonWrapper;
import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.MethodCallDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class MethodCallCommand {
	
	Logger logger = LoggerFactory.getLogger(MethodCallCommand.class);

	private static final String CLASS_NAME = "className";
	private static final String METHOD_NAME = "methodName";
	private static final String METHOD_PARAMETERS = "methodParameters";

	private static final String PARAMETER_TYPE_NAME = "parameterTypeName";

	private static final String ARGUMENT = "argument";
	
	public StringBuilder toJson(MethodCallDto methodCallDto) {
		StringBuilder translation = new StringBuilder();
		
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.METHOD_CALL);
		translation.append("\":{\"");
		translation.append(CLASS_NAME);
		translation.append("\":\"");
		translation.append(methodCallDto.className);
		translation.append("\"},\"");
		translation.append(METHOD_NAME);
		translation.append("\":\"");
		translation.append(methodCallDto.methodName);
		translation.append("\",\"");
		translation.append(METHOD_PARAMETERS);
		translation.append("\":[");
		
		boolean isFirstParameter = true;
		for (int i = 0; i < methodCallDto.args.length; i++) {
			
			translation.append("{");
			translation.append("\"");
			translation.append(PARAMETER_TYPE_NAME);
			translation.append("\":\"");
			translation.append(methodCallDto.parameterTypes[i].toString());
			translation.append("\", \"");
			translation.append(ARGUMENT);
			translation.append("\":");
			
			Object parameter = methodCallDto.args[i];
			JsonWrapper jsonWrapper = new JsonWrapper(parameter);
			translation.append(jsonWrapper.toJson());
			
			translation.append("}");
			if (isFirstParameter) {
				translation.append(",");
			} 
			isFirstParameter = false;
		}
		translation.append("]}}");
		
		return translation;
	}
	
	public MethodCallDto fromJson(StringBuilder action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action.toString());
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject methodCall = root.getAsJsonObject(CommandExpressor.METHOD_CALL);
				
		JsonElement innerObject = methodCall.get(CLASS_NAME);
		JsonPrimitive jsonPrimitive = innerObject.getAsJsonPrimitive(); 
		
		String className = jsonPrimitive.getAsString();
		
		innerObject = root.getAsJsonPrimitive(METHOD_NAME);
		String methodName = innerObject.getAsString();

		JsonArray jsonArray = root.getAsJsonArray(METHOD_PARAMETERS);
		
		String[] parameterTypeArray = new String[jsonArray.size()];
		
		Gson gson = new Gson();
		List<Object> argList = new ArrayList<Object>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			jsonObject = jsonElement.getAsJsonObject(); 

			jsonPrimitive = jsonObject.getAsJsonPrimitive(PARAMETER_TYPE_NAME);
			parameterTypeArray[i] = jsonPrimitive.getAsString();
			
			jsonObject = jsonObject.getAsJsonObject(ARGUMENT);
			
			String jsonWrapperJson = jsonObject.toString();
			
			JsonWrapper jsonWrapper = JsonWrapper.fromJson(gson, jsonWrapperJson);
			argList.add(jsonWrapper.object);
		}
		
		return new MethodCallDto(className, methodName, parameterTypeArray, argList.toArray());
	}
}
