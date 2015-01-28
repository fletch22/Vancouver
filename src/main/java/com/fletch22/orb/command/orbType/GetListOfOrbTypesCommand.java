package com.fletch22.orb.command.orbType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.GetListOfOrbTypesDto;
import com.fletch22.orb.command.orbType.dto.UpdateOrbTypeLabelDto;
import com.fletch22.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class GetListOfOrbTypesCommand {
	
	Logger logger = LoggerFactory.getLogger(UpdateOrbTypeLabelDto.class);
	
	@Autowired
	JsonUtil jsonUtil;

	public StringBuilder toJson(String searchString) {
		StringBuilder translation = new StringBuilder();

		translation.append("{'" + CommandExpressor.ROOT_LABEL + "':{'");

		translation.append(CommandExpressor.GET_LIST_OF_ORB_TYPES);
		translation.append("':'");
		translation.append(this.jsonUtil.escapeJsonIllegals(searchString));
		translation.append("'}}");

		return translation;
	}

	public GetListOfOrbTypesDto getActionData(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonElement command = root.get(CommandExpressor.GET_LIST_OF_ORB_TYPES);
				
		JsonPrimitive searchStringPrimitive = command.getAsJsonPrimitive();
		
		String searchString = this.jsonUtil.unescapeJsonIllegals(searchStringPrimitive.getAsString());
		
		return new GetListOfOrbTypesDto(searchString);
	}
}
