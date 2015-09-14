package com.fletch22.orb.command.orbType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.UpdateOrbTypeLabelDto;
import com.fletch22.util.json.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class UpdateOrbTypeLabelCommand {
	
	Logger logger = LoggerFactory.getLogger(UpdateOrbTypeLabelDto.class);
	
	@Autowired
	JsonUtil jsonUtil;

	public StringBuilder toJson(int orbInternalId, String orbTypeLabel) {
		StringBuilder translation = new StringBuilder();
		orbTypeLabel = this.jsonUtil.escapeJsonIllegals(orbTypeLabel);

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");

		translation.append(CommandExpressor.SET_ORB_TYPE_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId));
		translation.append("\",\"");
		translation.append(CommandExpressor.ORB_TYPE_LABEL);
		translation.append("\":\"");
		translation.append(this.jsonUtil.escapeJsonIllegals(orbTypeLabel));
		translation.append("\"");
		translation.append("}");
		translation.append("}}");

		return translation;
	}

	public UpdateOrbTypeLabelDto fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject addOrbType = root.getAsJsonObject(CommandExpressor.SET_ORB_TYPE_LABEL);
				
		JsonElement innerObject = addOrbType.get(CommandExpressor.ORB_TYPE_LABEL);
		JsonPrimitive typeLabelInner = innerObject.getAsJsonPrimitive();
		
		String label = this.jsonUtil.unescapeJsonIllegals(typeLabelInner.getAsString());
		
		innerObject = addOrbType.get(CommandExpressor.ORB_INTERNAL_ID);
		int orbTypeInternalId = innerObject.getAsJsonPrimitive().getAsInt();
		
		return new UpdateOrbTypeLabelDto(label, orbTypeInternalId);
	}
}
