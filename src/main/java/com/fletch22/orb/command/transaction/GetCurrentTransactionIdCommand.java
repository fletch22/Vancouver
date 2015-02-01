package com.fletch22.orb.command.transaction;

import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class GetCurrentTransactionIdCommand {

	public StringBuilder toJson() {
		StringBuilder translation = new StringBuilder();

		translation.append("{'");
		translation.append(CommandExpressor.SYSTEM_COMMAND);
		translation.append("':'");
		translation.append(CommandExpressor.GET_CURRENT_TRANSACTION_ID);
		translation.append("'}");

		return translation;
	}

	public void fromJson(String json) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(json);
		
		JsonPrimitive root = jsonObject.getAsJsonPrimitive(CommandExpressor.SYSTEM_COMMAND);
		String command = root.getAsString();
		
		if (!CommandExpressor.GET_CURRENT_TRANSACTION_ID.equals(command)) {
			throw new RuntimeException("Encountered problem parsing a json string thought to be a '" + GetCurrentTransactionIdCommand.class.getName() + "'.");
		}
	}
}
