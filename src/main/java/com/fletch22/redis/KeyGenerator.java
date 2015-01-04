package com.fletch22.redis;

public interface KeyGenerator {
	String getKey(String typeName);
	
	String getKeyPrefix();
}
