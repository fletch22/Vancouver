package com.fletch22.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.fletch22.orb.NakedOrb;

@Component
public class ObjectInstanceCacheService {

	@Autowired
	Jedis jedis;

	@Autowired
	RedisKeyFactory redisKeyFactory;

	KeyGenerator keyGenerator = null;

	@PostConstruct
	public void postConstruct() {
		this.keyGenerator = this.redisKeyFactory.getKeyGenerator(ObjectType.INSTANCE);
	}

	public void createInstance(NakedOrb nakedOrb) {

		Map<String, String> properties = nakedOrb.expressAllProperties();

		properties = filterNullProperties(properties);

		try {
			validateTypeProperties(properties);

			String key = this.keyGenerator.getKey(nakedOrb.getOrbInternalId());
			this.jedis.hmset(key, properties);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
	}

	private Map<String, String> filterNullProperties(Map<String, String> properties) {
		Map<String, String> filtered = new HashMap<String, String>();
		for (Entry<String, String> entry : properties.entrySet()) {
			if (entry.getValue() != null) {
				filtered.put(entry.getKey(), entry.getValue());
			}
		}
		return filtered;
	}

	private void validateTypeProperties(Map<String, String> properties) {
		if (null == properties) {
			throw new RuntimeException("Encountered problems validating the properties for a type. Properties were null. Should not be null.");
		}

		if (properties.size() == 0) {
			throw new RuntimeException("Encountered problems validating the properties for a type. There are zero properties. Properties should not be null.");
		}
	}
}
