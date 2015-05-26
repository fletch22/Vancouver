package com.fletch22.orb.command.orb;

import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class AddOrbCommand {

	public StringBuilder toJson(long orbTypeInternalId) {
		StringBuilder translation = new StringBuilder();
		
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ADD_ORB);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbTypeInternalId));
		translation.append("\"}");
		translation.append("}}");

		return translation;
	}
	
	public AddOrbDto fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject addOrbType = root.getAsJsonObject(CommandExpressor.ADD_ORB);
				
		JsonElement innerObject = addOrbType.get(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		JsonPrimitive typeLabelInner = innerObject.getAsJsonPrimitive(); 
		
		long orbTypeInternalId = typeLabelInner.getAsLong();
		
		AddOrbDto addOrbDto =  new AddOrbDto();
		addOrbDto.orbTypeInternalId = orbTypeInternalId;
		
		return addOrbDto;
	}
}
