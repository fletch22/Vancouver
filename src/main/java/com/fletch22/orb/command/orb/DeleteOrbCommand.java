package com.fletch22.orb.command.orb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.command.orbType.DeleteOrbDto;
import com.fletch22.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class DeleteOrbCommand {

	@Autowired
	JsonUtil jsonUtil;

	public StringBuilder toJson(long orbInternalId, boolean allowCascadingDeletes) {
		StringBuilder translation = new StringBuilder();
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.REMOVE_ORB_INSTANCE);
		translation.append("\":[{\"");
		translation.append(CommandExpressor.ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId));
		translation.append("\"},{\"");
		translation.append(CommandExpressor.ALLOW_CASCADING_DELETES);
		translation.append("\":\"");
		translation.append(String.valueOf(allowCascadingDeletes));
		translation.append("\"}]}}");

		return translation;
	}

	public DeleteOrbDto fromJson(String action) {

		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action);

		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonArray jsonArray = root.getAsJsonArray(CommandExpressor.REMOVE_ORB_INSTANCE);

		JsonObject firstElement = jsonArray.get(0).getAsJsonObject();
		JsonPrimitive orbInternalIdObject = firstElement.get(CommandExpressor.ORB_INTERNAL_ID).getAsJsonPrimitive();
		
		long orbInternalId = orbInternalIdObject.getAsLong();

		JsonObject secondElement = jsonArray.get(1).getAsJsonObject();
		JsonPrimitive allowCascadingDeletesJsonPrimitive = secondElement.getAsJsonPrimitive(CommandExpressor.ALLOW_CASCADING_DELETES).getAsJsonPrimitive();
		boolean allowCascadingDeletes = allowCascadingDeletesJsonPrimitive.getAsBoolean();

		return new DeleteOrbDto(orbInternalId, allowCascadingDeletes);
	}
}
