package com.fletch22.redis;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class JedisPoolFactory {

	private JedisPool jedisPool = null;
	
	public JedisPool getInstance() {
		if (null == this.jedisPool) {
			this.jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
		}
		return this.jedisPool;
	}
}
