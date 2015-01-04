package com.fletch22.redis;

public class OrbTypeKeyGenerator implements KeyGenerator {
	
	static final String KEY_PREFIX = "orbType:";

	@Override
	public String getKey(String typeName) {
		return getKeyPrefix() + typeName;
	}

	@Override
	public String getKeyPrefix() {
		return KEY_PREFIX;
	}
}
