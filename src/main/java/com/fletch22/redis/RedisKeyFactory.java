package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {
	
	@Autowired
	ObjectTypeKeyGenerator objectTypeKeyGenerator;
	
	@Autowired
	ObjectKeyGenerator objectKeyGenerator;

	public KeyGenerator getKeyGenerator(ObjectType orbType) {
		
		switch (orbType) {
			case TYPE:
				return this.objectTypeKeyGenerator;
			case INSTANCE:
				return this.objectKeyGenerator;
			default:
				throw new RuntimeException("Could not create key generator because type not recognized.");
		}
	}
}
