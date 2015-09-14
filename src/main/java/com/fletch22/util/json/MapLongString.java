package com.fletch22.util.json;

import java.util.Map;

import com.fletch22.orb.serialization.GsonSerializable;

public class MapLongString implements GsonSerializable {

	public Map<Long, String> map;
	
	public MapLongString(Map<Long, String> mapDeleted) {
		this.map = mapDeleted;
	}
}
