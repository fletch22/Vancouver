package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class ObjectBaseKeyGenerator implements KeyGenerator {
	
	@Autowired
	CacheProperties cacheProperties;
	
	@Override
	public String getKey(String id) {
		return getKeyPrefix() + id;
	}

	@Override
	public String getKeyPrefix() {
		return cacheProperties.getAppKeyPrefix() + ":" + getKeyCorePrefix();
	}

	@Override
	public String extractIdFromKey(String key) {
		return key.substring(getKeyPrefix().length());
	}
	
	public abstract String getKeyCorePrefix();
}
