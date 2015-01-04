package com.fletch22.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.fletch22.util.RandomUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext.xml")
public class ClientTest {
	
	Logger logger = LoggerFactory.getLogger(ClientTest.class);
	
	RandomUtil randomUtil = new RandomUtil();
	
	@Autowired
	RedisClient redisClient;

	@Test
	public void test() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		
		/// Jedis implements Closable. Hence, the Jedis instance will be auto-closed after the last statement.
		try (Jedis jedis = pool.getResource()) {
		  
		  /// ... do stuff here ... for example
		  jedis.set("foo", "bar");
		  String foobar = jedis.get("foo");
		  jedis.zadd("sose", 0, "car"); 
		  jedis.zadd("sose", 1, "bike");
		  //jedis.lpush
		  Set<String> sose = jedis.zrange("sose", 0, -1);
		  
		  printSetToConsole(sose);
		}
		
		/// ... when closing your application:
		pool.destroy();
	}

	private void printSetToConsole(Set<String> sose) {

		for (String thing: sose) {
			logger.info(thing);
		}
	}
	
	@Test
	public void testRedisClient() {
		
		int numInstances = 1000;
		
		// Arrange
		for (int i = 0; i < numInstances; i++) {
			createTypeForTest(redisClient, "Test-" + i);
		}
		
		Set<String> types = redisClient.getTypes();
		assertTrue("Should be true.", types.size() > 0);
		
		logger.info("Types size: {}", types.size());
	}

	private void createTypeForTest(RedisClient redisClient, String typeName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("prop1", "value1");
		map.put("prop2", "value2");
		map.put("prop3", "value3");
		map.put("prop4", "value4");
		
		redisClient.createType(typeName, map);
	}
	
	@Test
	@Ignore
	public void testFlushAll() {
		
		// Arrange
		// Act
		String result = redisClient.removeAllKeys();
		
		// Assert
		assertEquals("Should be ok.", Protocol.Keyword.OK.toString(), result);
		assertEquals("Should be no keys.", 0, redisClient.getTypes().size());
	}

}
