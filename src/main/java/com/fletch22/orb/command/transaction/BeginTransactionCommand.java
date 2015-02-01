package com.fletch22.orb.command.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.dto.UpdateOrbTypeLabelDto;
import com.fletch22.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class BeginTransactionCommand {

	Logger logger = LoggerFactory.getLogger(UpdateOrbTypeLabelDto.class);
	
	@Autowired
	JsonUtil jsonUtil;
	
	public StringBuilder toJson() {
		StringBuilder translation = new StringBuilder();

		translation.append("{'" + CommandExpressor.ROOT_LABEL + "':{'");
		translation.append(CommandExpressor.BEGIN_TRANSACTION);
		
		translation.append("':'placeholder'}}");

		return translation;
	}
	
	public void fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		root.getAsJsonPrimitive(CommandExpressor.BEGIN_TRANSACTION);
	}
}
