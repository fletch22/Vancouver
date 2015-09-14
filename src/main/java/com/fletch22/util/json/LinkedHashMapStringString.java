package com.fletch22.util.json;

import java.util.LinkedHashMap;

import com.fletch22.orb.serialization.GsonSerializable;

public class LinkedHashMapStringString implements GsonSerializable {

	public LinkedHashMap<String, String> linkedHashMap;
	
	public LinkedHashMapStringString(LinkedHashMap<String, String> linkedHashMap) {
		this.linkedHashMap = linkedHashMap;
	}
}
