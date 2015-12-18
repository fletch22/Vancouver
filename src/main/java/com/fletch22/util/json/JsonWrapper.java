package com.fletch22.util.json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.serialization.GsonSerializable;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class JsonWrapper {
	
	static transient Logger logger = LoggerFactory.getLogger(JsonWrapper.class);

	private static transient final Set<Class<?>> EASY_TO_SERIALIZE = getEasyToSerializeTypes();
	
	@SerializedName("clazzName")
	public String clazzName;
	
	@SerializedName("objectValueAsJson")
	private String objectValueAsJson;

	public transient Object object;

	transient Gson gson;
	
	public JsonWrapper(Object object, GsonFactory gsonFactory) {
		
		this.gson = gsonFactory.getInstance();

		ensureCanBeSerialized(object);

		this.object = object;

		if (this.object != null) {
			this.clazzName = this.object.getClass().getName();
		} else {
			this.clazzName = null;
		}

		this.objectValueAsJson = gson.toJson(this.object);
	}

	private void ensureCanBeSerialized(Object object) {
		if (object != null && !(object instanceof GsonSerializable) && !(isEasyToSerialize(object.getClass()))) {
			String clazzName = object.getClass().getSimpleName();
			throw new RuntimeException("Problem with '" + clazzName + "'. Either modify the JsonWrapper class to allow the serializer to natively serialize object or Object does not (but should) implement " + GsonSerializable.class.getSimpleName() + ".");
		}
	}

	public boolean isEasyToSerialize(Class<?> clazz) {
		return EASY_TO_SERIALIZE.contains(clazz);
	}

	private static Set<Class<?>> getEasyToSerializeTypes() {
		Set<Class<?>> easyToSerialize = new HashSet<Class<?>>();
		easyToSerialize.add(String.class);
		easyToSerialize.add(Boolean.class);
		easyToSerialize.add(Character.class);
		easyToSerialize.add(Byte.class);
		easyToSerialize.add(Short.class);
		easyToSerialize.add(Integer.class);
		easyToSerialize.add(Long.class);
		easyToSerialize.add(Float.class);
		easyToSerialize.add(Double.class);
		easyToSerialize.add(BigDecimal.class);
		return easyToSerialize;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public static JsonWrapper fromJson(Gson gson, String json) {

		JsonWrapper jsonWrapper = gson.fromJson(json, JsonWrapper.class);
		jsonWrapper.gson = gson;

		logger.info("Classname instantiating: {}", jsonWrapper.clazzName);
		
		try {
			if (jsonWrapper.clazzName == null) {
				jsonWrapper.object = null;
			} else {
				jsonWrapper.object = jsonWrapper.gson.fromJson(jsonWrapper.objectValueAsJson, Class.forName(jsonWrapper.clazzName));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return jsonWrapper;
	}
	
	public static class HashMapLongStringWrapper {
		public HashMap<Long, String> map;
	}
}