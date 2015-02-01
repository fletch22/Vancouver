package com.fletch22.orb.command.orbType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.GetJsonOrbTypeDto;
import com.fletch22.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class GetJsonOrbTypeCommand {

	@Autowired
	JsonUtil jsonUtil;
	
	public StringBuilder toJson(int orbTypeInternalId) {
		StringBuilder translation = new StringBuilder();

		String oidClean = this.jsonUtil.escapeJsonIllegals(String.valueOf(orbTypeInternalId));

		translation.append("{'");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("':{'");
		translation.append(CommandExpressor.GET_ORB_TYPE);
		translation.append("':{'");
		translation.append(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		translation.append("':'");
		translation.append(oidClean);
		translation.append("'}}}");

		return translation;
	}
	
	public GetJsonOrbTypeDto fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject getOrbType = root.getAsJsonObject(CommandExpressor.GET_ORB_TYPE);
		
		JsonPrimitive orbTypeInternalIdJsonObject = getOrbType.getAsJsonPrimitive(CommandExpressor.ORB_TYPE_INTERNAL_ID);
	
		int orbTypeInternalId = orbTypeInternalIdJsonObject.getAsInt();
		
		return new GetJsonOrbTypeDto(orbTypeInternalId);
	}
}
