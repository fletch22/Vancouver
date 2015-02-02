package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ObjectTypeKeyGenerator implements KeyGenerator {
	
	@Value("${cache.app.prefix}")
	String cachAppPrefix;
	
	private static final String KEY_CORE_PREFIX = "orbType:";

	@Override
	public String getKey(String id) {
		return getKeyPrefix() + id;
	}

	@Override
	public String getKeyPrefix() {
		return cachAppPrefix + ":" + KEY_CORE_PREFIX;
	}

	@Override
	public String extractIdFromKey(String key) {
		return key.substring(getKeyPrefix().length());
	}
}
