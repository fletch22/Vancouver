package com.fletch22.util.json;

import java.util.LinkedHashSet;

import com.fletch22.orb.serialization.GsonSerializable;

public class LinkedHashSetString implements GsonSerializable {

	public LinkedHashSet<String> linkedHashSet;
	
	public LinkedHashSetString(LinkedHashSet<String> linkedHashSet) {
		this.linkedHashSet = linkedHashSet;
	}
}
