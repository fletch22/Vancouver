package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObjectTypeKeyGenerator implements KeyGenerator {
	
	@Autowired
	CacheProperties cacheProperties;
	
	private static final String KEY_CORE_PREFIX = "orbType:";

	@Override
	public String getKey(String id) {
		return getKeyPrefix() + id;
	}

	@Override
	public String getKeyPrefix() {
		return cacheProperties.getAppKeyPrefix() + ":" + KEY_CORE_PREFIX;
	}

	@Override
	public String extractIdFromKey(String key) {
		return key.substring(getKeyPrefix().length());
	}
}
