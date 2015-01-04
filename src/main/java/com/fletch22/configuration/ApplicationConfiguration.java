package com.fletch22.configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fletch22.redis.JedisFactory;

import redis.clients.jedis.Jedis;

@Configuration
public class ApplicationConfiguration {
	
	@Autowired
	JedisFactory jedisFactory;

	@Bean
	public Jedis getJedis() {
		return this.jedisFactory.getInstance();
	}
}
