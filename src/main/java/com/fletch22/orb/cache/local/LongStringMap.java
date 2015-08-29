package com.fletch22.orb.cache.local;

import java.util.Map;

import com.fletch22.orb.serialization.JsonSerializable;

public class LongStringMap implements JsonSerializable {

	public Map<Long, String> map;
	
	public LongStringMap(Map<Long, String> mapDeleted) {
		this.map = mapDeleted;
	}
}
