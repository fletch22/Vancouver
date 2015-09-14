package com.fletch22.orb.command.orbType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.OrbTypeConstants;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.util.json.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class AddOrbTypeCommand {
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	public StringBuilder toJson(String orbLabel) {
		 return this.toJson(orbLabel, OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET);
	}
	
	public StringBuilder toJson(String orbLabel, long orbTypeInternalId) {
		StringBuilder translation = new StringBuilder();
		
		String orbLabelClean = this.jsonUtil.escapeJsonIllegals(orbLabel);

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ADD_ORB_TYPE);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ORB_TYPE_LABEL);
		translation.append("\":\"");
		translation.append(orbLabelClean);
		translation.append("\",\"");
		translation.append(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbTypeInternalId));
		translation.append("\"}");
		translation.append("}}");

		return translation;
	}
	
	public AddOrbTypeDto fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject addOrbType = root.getAsJsonObject(CommandExpressor.ADD_ORB_TYPE);
				
		JsonElement innerObject = addOrbType.get(CommandExpressor.ORB_TYPE_LABEL);
		JsonPrimitive typeLabelInner = innerObject.getAsJsonPrimitive(); 
		
		String label = this.jsonUtil.unescapeJsonIllegals(typeLabelInner.getAsString());
		
		innerObject = addOrbType.get(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		int orbTypeInternalId = innerObject.getAsJsonPrimitive().getAsInt();
		
		return new AddOrbTypeDto(label, orbTypeInternalId);
	}
}
