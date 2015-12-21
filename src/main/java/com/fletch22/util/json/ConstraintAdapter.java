package com.fletch22.util.json;

import java.lang.reflect.Type;

import com.fletch22.orb.query.constraint.Constraint;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ConstraintAdapter extends JsonAdapter implements JsonSerializer<Constraint>, JsonDeserializer<Constraint> {

	@Override
	public JsonElement serialize(Constraint src, Type typeOfSrc, JsonSerializationContext context) {
		return serializeObject(src, typeOfSrc, context);
	}
	
	@Override
	public Constraint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return (Constraint) deserializeToObject(json, typeOfT, context);
	}
}
