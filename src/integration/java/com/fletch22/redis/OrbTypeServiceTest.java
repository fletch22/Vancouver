package com.fletch22.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
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

import com.fletch22.orb.IntegrationTests;
import com.fletch22.orb.cache.external.NakedOrb;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.service.OrbTypeService;
import com.fletch22.util.JsonUtil;
import com.fletch22.util.RandomUtil;
import com.google.gson.Gson;

@org.junit.experimental.categories.Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class OrbTypeServiceTest {
	
	Logger logger = LoggerFactory.getLogger(OrbTypeServiceTest.class);
	
	@Autowired
	ObjectTypeCacheService objectTypeCacheService;
	
	@Autowired
	RandomUtil randomUtil;
	
	@Autowired
	OrbTypeService orbTypeService;
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;
	
	@After
	public void afterClass() {
		
		Set<String> keys = objectTypeCacheService.getTypes();
		for (String key: keys) {
			String id = objectTypeCacheService.typeKeyGenerator.extractIdFromKey(key);
			objectTypeCacheService.deleteType(id);
		}
	}
	
	@Test
	public void pureJavaCreateTest() {
		String label = randomUtil.getRandomString();
		int max = 100;
		
		logger.info("Start create type.");
		for (int i = 0; i < max; i++) {
			String labelToUse = label + String.valueOf(i);
			long orbInternalId = orbTypeService.addOrbType(labelToUse);
		}
		logger.info("End create type.");
	}
	
	@Test
	public void testJson() {
		
		String illegal = "1 2 > '\\' 3 = / 4";
	
		Gson gson = new Gson();	
		String parsed = gson.toJson(illegal);
		
		logger.info("JP: {}", parsed);
		logger.info("JP P: {}", gson.fromJson(parsed, String.class));
		
		JsonUtil jsonUtil = new JsonUtil();
		logger.info("Ju: {}", jsonUtil.escapeJsonIllegals(illegal));
	}

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
	public void testCreateType() {
		
		int numInstances = 10000;
		
		List<String> uniqueGuids = getUniqueUUIDs(numInstances);
		
		// Arrange
		logger.info("Start create type test.");
		for (int i = 0; i < numInstances; i++) {
			createTypeForTest(objectTypeCacheService, "Test-" + uniqueGuids.get(i));
		}
		logger.info("End create type test.");
		
		Set<String> types = objectTypeCacheService.getTypes();
		assertTrue("Should be true.", types.size() > 0);
		
		logger.info("Types size: {}", types.size());
	}
	
	private List<String> getUniqueUUIDs(int number) {
		
		List<String> uniqueKeys = new ArrayList<String>(number);
		
		for (int i = 0; i < number; i++) {
			uniqueKeys.add(java.util.UUID.randomUUID().toString());
		}

		return uniqueKeys;
	}

	private void createTypeForTest(ObjectTypeCacheService redisClient, String typeName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("prop1", "value1");
		map.put("prop2", "value2");
		map.put("prop3", "value3");
		map.put("prop4", "value4");
		
		NakedOrb nakedOrb = new NakedOrb(map);
		
		redisClient.createType(nakedOrb);
	}
	
	@Test
	@Ignore
	public void testFlushAll() {
		
		// Arrange
		// Act
		String result = objectTypeCacheService.removeAllKeys();
		
		// Assert
		assertEquals("Should be ok.", Protocol.Keyword.OK.toString(), result);
		assertEquals("Should be no keys.", 0, objectTypeCacheService.getTypes().size());
	}
	
	@Test
	public void removeType() {

		// Arrange
		String id = getRandomTypeName();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		
		NakedOrb nakedOrb = new NakedOrb(map);
		
		objectTypeCacheService.createType(nakedOrb);
		
		map = objectTypeCacheService.getType(id);
		
		assertNotNull("Returned object should not be null.", map);
		
		objectTypeCacheService.deleteType(id);
		
		// Act
		boolean exceptionThrown = false;
		try {
			NakedOrb result = objectTypeCacheService.deleteType(id);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		
		// Assert
		assertTrue("The exception should have been thrown because no id exists.", exceptionThrown);
	}

	private String getRandomTypeName() {
		return this.randomUtil.getRandomInteger() + "-" + DateTime.now().toString();
	}
	
	@Test
	public void createFailWithNullProperties() {

		// Arrange
		String typeName = getRandomTypeName();
		
		String message = null;
		
		NakedOrb nakedOrb = new NakedOrb(null);
		
		// Act
		try {
			objectTypeCacheService.createType(nakedOrb);
		} catch (Exception e) {
			message = e.getMessage();
			logger.info(message);
		}
		
		// Assert
		assertNotNull("There should be a message and it should not be null.", message);
		assertEquals("Error message should be expected.", "Encountered problem while trying to create type: Encountered problems validating the properties for a type. Properties were null. Should not be null.", message);
	}
	
	@Test
	public void testRemoveRedisPropertyFromHashMap() {
		
		// Arrange
		String propertyToRemove = "test1";
		int id = this.randomUtil.getRandomInteger();
		Map<String, String> map = new HashMap<String, String>();
		map.put(propertyToRemove, "value1");
		map.put("test2", "value2");
		
		NakedOrb nakedOrb = new NakedOrb(map);
		nakedOrb.setId(id);
		
		objectTypeCacheService.createType(nakedOrb);
		
		// Act
		objectTypeCacheService.removePropertyFromType(String.valueOf(id), propertyToRemove);
		
		// Assert
		map = objectTypeCacheService.getType(String.valueOf(id));
		assertFalse("Property list should no longer contain property '" + propertyToRemove, map.containsKey(propertyToRemove));
		
		objectTypeCacheService.deleteType(String.valueOf(id));
	}
	
	@Test
	public void testRemoveRedisPropertyDoesNotExistFromHashMap() {
		
		// Arrange
		String propertyDoesNotExist = "test1";
		int id = this.randomUtil.getRandomInteger();
		Map<String, String> map = new HashMap<String, String>();
		map.put("test2", "value2");
		
		NakedOrb nakedOrb = new NakedOrb(map);
		nakedOrb.setId(id);
		
		objectTypeCacheService.createType(nakedOrb);
		
		// Act
		boolean exceptionThrown = false;
		try {
			objectTypeCacheService.removePropertyFromType(nakedOrb.getOrbInternalId(), propertyDoesNotExist);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		
		// Assert
		assertFalse("Exception should not be thrown. Non existent field should not cause an exception", exceptionThrown);
		
		objectTypeCacheService.deleteType(String.valueOf(id));
	}
	
	@Test
	public void testRenameRedisPropertyInRedisHashMap() {
		
		// Arrange
		String propertyNameOriginal = "test1";
		int id = this.randomUtil.getRandomInteger();
		Map<String, String> map = new HashMap<String, String>();
		map.put(propertyNameOriginal, "value2");
		map.put("test2", "value2");
		
		NakedOrb nakedOrb = new NakedOrb(map);
		nakedOrb.setId(id);
		
		objectTypeCacheService.createType(nakedOrb);
		
		String propertyNameNew = "righteousBison";
		
		// Act
		objectTypeCacheService.renameTypeProperty(String.valueOf(id), propertyNameOriginal, propertyNameNew);
		
		map = objectTypeCacheService.getType(String.valueOf(id));
		
		for (String keyThing: map.keySet()) {
			logger.info("Key: Value: {}, {}", keyThing, map.get(keyThing));
		}
		
		// Assert
		assertTrue("Returned properties should have more than one prop.", map.size() > 0);
		assertTrue("Properties should contain new property.", map.containsKey(propertyNameNew));
		assertFalse("Properties should not contain original property.", map.containsKey(propertyNameOriginal));
	}

}
