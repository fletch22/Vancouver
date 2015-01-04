package com.fletch22.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

@Component
public class JedisFactory {

	@Autowired
	JedisPoolFactory jedisPoolFactory;
	
	public Jedis getInstance()  {
		return this.jedisPoolFactory.getInstance().getResource();
	}
	
}
