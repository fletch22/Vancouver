package com.fletch22.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {

	public KeyGenerator getKeyGenerator(OrbType orbType) {
		
		if (orbType.equals(OrbType.TYPE)) {
			return new OrbTypeKeyGenerator();
		} else {
			throw new RuntimeException("Could not create key generator because type not recognized.");
		}
	}
}
