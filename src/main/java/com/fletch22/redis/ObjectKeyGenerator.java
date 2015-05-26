package com.fletch22.redis;

import org.springframework.stereotype.Component;

@Component
public class ObjectKeyGenerator extends ObjectBaseKeyGenerator implements KeyGenerator {
	
	private static final String KEY_CORE_PREFIX = "orbInstance:";

	@Override
	public String getKeyCorePrefix() {
		return KEY_CORE_PREFIX;
	}
}
