package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {
	
	@Autowired
	ObjectTypeKeyGenerator objectTypeKeyGenerator;

	public KeyGenerator getKeyGenerator(ObjectType orbType) {
		
		if (orbType.equals(ObjectType.TYPE)) {
			return this.objectTypeKeyGenerator;
		} else {
			throw new RuntimeException("Could not create key generator because type not recognized.");
		}
	}
}
