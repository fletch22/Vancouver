package com.fletch22.orb.command.transaction;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.transaction.dto.KillTransactionDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class KillTransactionCommand {

	public StringBuilder toJson(BigDecimal transactionId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{'");
		translation.append(CommandExpressor.SYSTEM_COMMAND);
		translation.append("':{'");
		translation.append(CommandExpressor.KILL_TRANSACTION);
		translation.append("':'");
		translation.append(transactionId.toString());
		translation.append("'}}");

		return translation;
	}

	public KillTransactionDto fromJson(String action) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.SYSTEM_COMMAND);
		JsonPrimitive jsonPrimitive = root.getAsJsonPrimitive(CommandExpressor.KILL_TRANSACTION);
		BigDecimal transactionId = jsonPrimitive.getAsBigDecimal();
		
		return new KillTransactionDto(transactionId);
	}
}
