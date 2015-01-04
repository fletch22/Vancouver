package com.fletch22.redis;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

@Component
public class RedisClient {
	
	Logger logger = LoggerFactory.getLogger(RedisClient.class);
	
	@Autowired
	Jedis jedis;
	
	@Autowired
	RedisKeyFactory redisKeyFactory;
	
	KeyGenerator typeKeyGenerator = null;

	@PostConstruct
	public void postConstruct() {
		this.typeKeyGenerator = this.redisKeyFactory.getKeyGenerator(OrbType.TYPE);
	}

	public void createType(String typeName, Map<String, String> properties) {
		
		try {
			String key = this.typeKeyGenerator.getKey(typeName);
			this.jedis.hmset(key, properties);
		} catch (Exception e) {
			logger.error("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
	}
	
	public Set<String> getTypes() {
		Set<String> results = new HashSet<String>();
		try {
			results = this.jedis.keys(this.typeKeyGenerator.getKeyPrefix() + "*");
		} catch (Exception e) {
			logger.error("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return results;
	}

	public String removeAllKeys() {
		String result = null;
		try {
			result = this.jedis.flushAll();
		} catch (Exception e) {
			logger.error("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return result;
	}
}
