package com.fletch22.orb.command.transaction;

import java.math.BigDecimal;

import com.fletch22.orb.CommandExpressor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class CommitTransactionCommand {

	public StringBuilder toJson(BigDecimal transactionId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.COMMIT_TRANSACTION_WITH_ID);
		translation.append("\":\"");
		translation.append(transactionId);
		translation.append("\"}}");

		return translation;
	}

	public CommitTransactionDto fromJson(String action) {
		CommitTransactionDto commitTransactionDto = new CommitTransactionDto();
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonPrimitive jsonPrimitive = root.getAsJsonPrimitive(CommandExpressor.COMMIT_TRANSACTION_WITH_ID);
		
		commitTransactionDto.tranId = jsonPrimitive.getAsBigDecimal();

		return commitTransactionDto;
	}
}
