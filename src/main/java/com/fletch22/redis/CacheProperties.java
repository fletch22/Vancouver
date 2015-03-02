package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheProperties {

	@Value("${cache.app.prefix}")
	private String cacheAppPrefix;
	
	public String getAppKeyPrefix() {
		return this.cacheAppPrefix;
	}
	
}
