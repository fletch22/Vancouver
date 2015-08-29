package com.fletch22.aop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.serialization.JsonSerializable;
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

	public JsonWrapper(Object object) {

		ensureCanBeSerialized(object);

		this.object = object;

		if (this.object != null) {
			this.clazzName = this.object.getClass().getName();
		} else {
			this.clazzName = null;
		}
		this.gson = new Gson();
		this.objectValueAsJson = gson.toJson(this.object);
	}


	private Object wrapInGenericWrapper(Object object) {
		String clazz = object.getClass().getName();
		
		if (clazz.equals(HashMap.class.getName()) ) {
			HashMap hashMap = (HashMap) object;
			if (!hashMap.isEmpty()) {
				Object firstKey = hashMap.keySet().iterator().next();
				Object entry = hashMap.get(firstKey);
				
				if (firstKey.getClass().getName().equals(Long.class.getName())) {
					HashMapLongStringWrapper wrapper = new HashMapLongStringWrapper();
					wrapper.map = (HashMap<Long, String>) object;
					object = wrapper;
				}
			} 
		} else {
			throw new RuntimeException("Special type not set up yet with special special wrapper.");
		}
		
		return object;
	}


	private void ensureCanBeSerialized(Object object) {
		if (object != null && !(object instanceof JsonSerializable) && !(isEasyToSerialize(object.getClass()))) {
			String clazzName = object.getClass().getSimpleName();
			throw new RuntimeException("Problem with '" + clazzName + "'. Either modify the JsonWrapper class to allow the serializer to natively serialize object or Object does not (but should) implement " + JsonSerializable.class.getSimpleName() + ".");
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