package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class OrbTransformer {
	
	Logger logger = LoggerFactory.getLogger(OrbTransformer.class);
	
	private static final String ROOT_LABEL = "orb";

	private static final String USER_DEFINED_PROPERTIES = "userDefinedProperties";
	
	@Autowired
	JsonUtil jsonUtil;
	
	public StringBuilder convertToJson(Orb orb) {
		StringBuilder json = new StringBuilder("{'");
		
		json.append(OrbTransformer.ROOT_LABEL);
		json.append("':{'");
		json.append(USER_DEFINED_PROPERTIES);
		json.append("':[");
		
		Map<String, String> userDefinedProperties = orb.getUserDefinedProperties();
		
		Set<String> keySet = userDefinedProperties.keySet();

		int i = 0;
		int size = keySet.size();
		for (String key: keySet) 
		{
			json.append("{'");
            json.append(this.jsonUtil.escapeJsonIllegals(key));
			json.append("':'");
			json.append(this.jsonUtil.escapeJsonIllegals(userDefinedProperties.get(key)));
			json.append("'}");
			if (i < size - 1) {
				json.append(",");
			}
			
			i++;
		}

		json.append("],");
		json.append("'" + CommandExpressor.ORB_INTERNAL_ID + "':'");
		json.append(orb.getOrbInteralId());
        json.append("','" + CommandExpressor.ORB_TYPE_INTERNAL_ID + "':'");
		json.append(orb.getOrbTypeInternalId());
		json.append("','" + CommandExpressor.ORB_TRAN_DATE + "':'");
		json.append(orb.getTranDate().toString());
		json.append("'}}");

		return json;
	}
	
	public Orb convertFromJson(String json) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(json);

		JsonObject root = jsonObject.getAsJsonObject(OrbTransformer.ROOT_LABEL);
		JsonArray jsonArray = root.getAsJsonArray(OrbTransformer.USER_DEFINED_PROPERTIES);

		LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement arrayElement = jsonArray.get(i);
			JsonObject arrayObject = arrayElement.getAsJsonObject();
			
		    for (Entry<String, JsonElement> entry : arrayObject.entrySet()) {
		        String key = entry.getKey();
		        JsonPrimitive valueObject = entry.getValue().getAsJsonPrimitive();
		        properties.put(key, valueObject.getAsString());
		    }
		}
		
		JsonPrimitive jsonPrimitive = root.getAsJsonPrimitive(CommandExpressor.ORB_INTERNAL_ID);
		long orbInternalId = jsonPrimitive.getAsLong();
		
		jsonPrimitive = root.getAsJsonPrimitive(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		long orbTypeInternalId = jsonPrimitive.getAsLong();
		
		jsonPrimitive = root.getAsJsonPrimitive(CommandExpressor.ORB_TRAN_DATE);
		BigDecimal tranDate = new BigDecimal(jsonPrimitive.getAsString());
		
		return new Orb(orbInternalId, orbTypeInternalId, tranDate, properties);
	}
}
