package com.fletch22.orb.command.orbType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbTransformer;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.external.OrbTypeManagerForExternalCache;
import com.fletch22.orb.command.orbType.dto.AddWholeOrbTypeDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class AddWholeOrbTypeCommand {
	
	@Autowired
	@Qualifier(value = OrbTypeManagerForExternalCache.COMPONENT_QUALIFIER_ID)
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbTransformer orbTransformer;

	public StringBuilder toJson(Orb orb) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");

		translation.append(CommandExpressor.ORB_ADD_WHOLE);
		translation.append("\":");
		translation.append(orbTransformer.convertToJson(orb));
		translation.append("}}");

		return translation;
	}
	
	public AddWholeOrbTypeDto fromJson(String json) {
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(json);

		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonObject jsonOrb = root.getAsJsonObject(CommandExpressor.ORB_ADD_WHOLE);

		return new AddWholeOrbTypeDto(orbTransformer.convertFromJson(jsonOrb.toString()));
	}
}
