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
import com.fletch22.util.RandomUtil;

@org.junit.experimental.categories.Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext.xml")
public class OrbTypeServiceTest {
	
	Logger logger = LoggerFactory.getLogger(OrbTypeServiceTest.class);
	
	@Autowired
	ObjectTypeCacheService orbTypeService;
	
	@Autowired
	RandomUtil randomUtil;

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
			createTypeForTest(orbTypeService, "Test-" + uniqueGuids.get(i));
		}
		logger.info("End create type test.");
		
		Set<String> types = orbTypeService.getTypes();
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
		
		redisClient.createType(typeName, map);
	}
	
	@Test
	@Ignore
	public void testFlushAll() {
		
		// Arrange
		// Act
		String result = orbTypeService.removeAllKeys();
		
		// Assert
		assertEquals("Should be ok.", Protocol.Keyword.OK.toString(), result);
		assertEquals("Should be no keys.", 0, orbTypeService.getTypes().size());
	}
	
	@Test
	public void removeType() {

		// Arrange
		String typeName = getRandomTypeName();
		
		orbTypeService.createType(typeName,  new HashMap<String, String>());
		
		Map<String, String> map = orbTypeService.getType(typeName);
		
		assertNotNull("Returned object should not be null.", map);
		
		// Act
		boolean result = orbTypeService.removeType(typeName);
		
		// Assert
		assertTrue("Returned result from removal should be true.", result);
		assertFalse("Orb type should not exist.", orbTypeService.doesObjectTypeExist(typeName));
	}

	private String getRandomTypeName() {
		return this.randomUtil.getRandomInteger() + "-" + DateTime.now().toString();
	}
	
	@Test
	public void createFailWithNullProperties() {

		// Arrange
		String typeName = getRandomTypeName();
		
		String message = null;
		
		// Act
		try {
			orbTypeService.createType(typeName, null);
		} catch (Exception e) {
			message = e.getMessage();
			logger.info(message);
		}
		
		// Assert
		assertNotNull("There should be a message and it should not be null.", message);
		assertEquals("Error message should be expected.", "Encountered problem while trying to create type: Encountered problems validating the properties for a type. Properties were null. Should not be null.", message);
	}
	
	@Test
	public void testRename() {
		
		// Arrange
		String typeName = getRandomTypeName();
		orbTypeService.createType(typeName, new HashMap<String, String>());
		
		String newTypeName = getRandomTypeName();
		
		// Act
		assertFalse("Record with new name should not exist.", orbTypeService.doesObjectTypeExist(newTypeName));
		assertTrue("Record with old name should exist.", orbTypeService.doesObjectTypeExist(typeName));
		orbTypeService.renameType(typeName, newTypeName);
		
		// Assert
		assertFalse("Record with old name should not exist.", orbTypeService.doesObjectTypeExist(typeName));
		assertTrue("Record with new name should exist.", orbTypeService.doesObjectTypeExist(newTypeName));
		
		orbTypeService.removeType(newTypeName);
	}
	
	@Test
	public void testRemoveRedisPropertyFromHashMap() {
		
		// Arrange
		String propertyToRemove = "test1";
		String typeName = getRandomTypeName();
		Map<String, String> map = new HashMap<String, String>();
		map.put(propertyToRemove, "value1");
		map.put("test2", "value2");
		
		orbTypeService.createType(typeName, map);
		
		// Act
		orbTypeService.removePropertyFromType(typeName, propertyToRemove);
		
		// Assert
		map = orbTypeService.getType(typeName);
		assertFalse("Property list should no longer contain property '" + propertyToRemove, map.containsKey(propertyToRemove));
		
		orbTypeService.removeType(typeName);
	}
	
	@Test
	public void testRemoveRedisPropertyDoesNotExistFromHashMap() {
		
		// Arrange
		String propertyDoesNotExist = "test1";
		String typeName = getRandomTypeName();
		Map<String, String> map = new HashMap<String, String>();
		map.put("test2", "value2");
		
		orbTypeService.createType(typeName, map);
		
		// Act
		boolean exceptionThrown = false;
		try {
			orbTypeService.removePropertyFromType(typeName, propertyDoesNotExist);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		
		// Assert
		assertFalse("Exception should not be thrown. Non existent field should not cause an exception", exceptionThrown);
		
		orbTypeService.removeType(typeName);
	}
	
	@Test
	public void testRenameRedisPropertyInRedisHashMap() {
		
		// Arrange
		String propertyNameOriginal = "test1";
		String typeName = getRandomTypeName();
		Map<String, String> map = new HashMap<String, String>();
		map.put(propertyNameOriginal, "value2");
		map.put("test2", "value2");
		
		orbTypeService.createType(typeName, map);
		
		String propertyNameNew = "righteousBison";
		
		// Act
		orbTypeService.renameTypeProperty(typeName, propertyNameOriginal, propertyNameNew);
		
		map = orbTypeService.getType(typeName);
		
		for (String keyThing: map.keySet()) {
			logger.info("Key: Value: {}, {}", keyThing, map.get(keyThing));
		}
		
		// Assert
		assertTrue("Returned properties should have more than one prop.", map.size() > 0);
		assertTrue("Properties should contain new property.", map.containsKey(propertyNameNew));
		assertFalse("Properties should not contain original property.", map.containsKey(propertyNameOriginal));
	}

}
