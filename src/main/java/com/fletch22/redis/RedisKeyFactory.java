package com.fletch22.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {

	public KeyGenerator getKeyGenerator(ObjectType orbType) {
		
		if (orbType.equals(ObjectType.TYPE)) {
			return new ObjectTypeKeyGenerator();
		} else {
			throw new RuntimeException("Could not create key generator because type not recognized.");
		}
	}
}
