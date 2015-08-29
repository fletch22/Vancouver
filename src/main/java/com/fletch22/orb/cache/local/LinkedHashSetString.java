package com.fletch22.orb.cache.local;

import java.util.LinkedHashSet;

import com.fletch22.orb.serialization.JsonSerializable;

public class LinkedHashSetString implements JsonSerializable {

	public LinkedHashSet<String> linkedHashSet;
	
	public LinkedHashSetString(LinkedHashSet<String> linkedHashSet) {
		this.linkedHashSet = linkedHashSet;
	}
	
}
