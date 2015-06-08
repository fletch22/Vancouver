package com.fletch22.redis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.fletch22.orb.cache.local.CacheService;

//@Component
public class ObjectAppCacheService implements CacheService {
	
	@Autowired
	Jedis jedis;
	
	@Autowired
	CacheProperties cacheProperties;
	
	public void clearAllItemsFromCache() {
		try {
			Set<String> allAppKeys = this.jedis.keys(this.cacheProperties.getAppKeyPrefix() + "*");
			for (String key : allAppKeys) {
				this.jedis.del(key);
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
	}
}
