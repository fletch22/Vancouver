package com.fletch22.orb.command.orbType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.GetOrbTypeFromLabelDto;
import com.fletch22.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class GetOrbTypeFromLabelCommand {

	
	@Autowired
	JsonUtil jsonUtil;
	
	public StringBuilder toJson(String label) {
		StringBuilder sb = new StringBuilder();

		label = this.jsonUtil.escapeJsonIllegals(label);
		sb.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + CommandExpressor.GET_ORB_TYPE_FROM_LABEL + "\":{\"" + CommandExpressor.ORB_TYPE_LABEL + "\":\"" + label + "\"}}}");

		return sb;
	}
	
	public GetOrbTypeFromLabelDto fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject addOrbType = root.getAsJsonObject(CommandExpressor.GET_ORB_TYPE_FROM_LABEL);
		
		JsonPrimitive jsonPrimitive = addOrbType.getAsJsonPrimitive(CommandExpressor.ORB_TYPE_LABEL);
		
		return new GetOrbTypeFromLabelDto(jsonPrimitive.getAsString());
	}
}
