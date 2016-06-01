package com.fletch22.util.json;

import java.lang.reflect.Type;

import com.fletch22.orb.query.criteria.Criteria;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CriteriaAdapter extends JsonAdapter implements JsonSerializer<Criteria>, JsonDeserializer<Criteria> {

	@Override
	public JsonElement serialize(Criteria src, Type typeOfSrc, JsonSerializationContext context) {
		return serializeObject(src, typeOfSrc, context);
	}
	
	public Criteria deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return (Criteria) deserializeToObject(jsonElement, typeOfT, context);
	}
}
