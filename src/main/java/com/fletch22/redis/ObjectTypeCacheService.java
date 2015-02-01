package com.fletch22.redis;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

@Component
public class ObjectTypeCacheService {
	
	private static final long DELETE_KEY_RESULT_KEY_NOT_FOUND = 0;
	private static final String DEFAULT_KEY_TRAN_DATE = "tranDate";
	private static final long RENAME_RESULT_KEY_ALREADY_EXISTS = 0;
	private static final long REMOVE_PROPERTY_FROM_HASHMAP_PROPERTY_NOT_FOUND = 0;

	Logger logger = LoggerFactory.getLogger(ObjectTypeCacheService.class);
	
	@Autowired
	Jedis jedis;
	
	@Autowired
	RedisKeyFactory redisKeyFactory;
	
	KeyGenerator typeKeyGenerator = null;

	@PostConstruct
	public void postConstruct() {
		this.typeKeyGenerator = this.redisKeyFactory.getKeyGenerator(ObjectType.TYPE);
	}

	public void createType(String typeName, Map<String, String> properties) {
		
		try {
			validateTypeProperties(properties);
			Map<String, String> map = transformToObjectProperties(properties);

			String key = this.typeKeyGenerator.getKey(typeName);
			this.jedis.hmset(key, map);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
	}
	
	private void validateTypeProperties(Map<String, String> properties) {
		if (null == properties) {
			throw new RuntimeException("Encountered problems validating the properties for a type. Properties were null. Should not be null.");
		}
	}

	public Set<String> getTypes() {
		Set<String> results = new HashSet<String>();
		try {
			results = this.jedis.keys(this.typeKeyGenerator.getKeyPrefix() + "*");
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return results;
	}
	
	public boolean removeType(String typeName) {
		boolean wasSuccessful = false;
		try {
			String key = getTypeNameKey(typeName);
			long result = jedis.del(getTypeNameKey(typeName));
			if (result == DELETE_KEY_RESULT_KEY_NOT_FOUND) {
				String message = String.format("Encountered problem with removing type. Could not find item with key '" + key + "' for type '" + typeName + "'. Because not found could not delete.");
				throw new RuntimeException(message);
			}
			wasSuccessful = true;
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return wasSuccessful;
	}
	
	private String getTypeNameKey(String typeName) {
		return this.typeKeyGenerator.getKeyPrefix() + typeName;
	}

	public String removeAllKeys() {
		String result = null;
		try {
			result = this.jedis.flushAll();
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return result;
	}

	public Map<String, String> getType(String typeName) {
		Map<String, String> result = null;
		try {
			String key = getTypeNameKey(typeName);
			result = this.jedis.hgetAll(key);
			if (!doesHashMapRecordExist(result)) {
				throw new RuntimeException("Encountered problem trying to get non-existent type '" + typeName + "' with key '" + key + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return result;
	}
	
	public boolean doesObjectTypeExist(String typeName) {
		boolean exists = false;
		try {
			String key = getTypeNameKey(typeName);
			exists = this.jedis.exists(key);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to determine if type exists: " + e.getMessage(), e);
		}
		return exists;
	}
	
	private boolean doesHashMapRecordExist(Map<String, String> returnedMap) {
		boolean result = true;
		if (returnedMap.size() == 0) {
			result = false;
		}
		return result;
	}
	
	public Map<String, String> transformToObjectProperties(Map<String, String> map) {
		Map<String, String> orbProperties = new HashMap<String, String>();
		
		if (map.containsKey(DEFAULT_KEY_TRAN_DATE)) {
			throw new RuntimeException("Encountered problem while trying to convert type properties. Properties were found to already have a key '" + DEFAULT_KEY_TRAN_DATE + "'. This key is reserved and cannot be specified by user.");
		}
		orbProperties.put(DEFAULT_KEY_TRAN_DATE, String.valueOf(DateTime.now().getMillis()));
		orbProperties.putAll(map);
		 
		return orbProperties;
	}

	public void renameType(String typeName, String newTypeName) {
		try {
			long result = this.jedis.renamenx(getTypeNameKey(typeName), getTypeNameKey(newTypeName));
			if (result == RENAME_RESULT_KEY_ALREADY_EXISTS) {
				throw new RuntimeException("Encountered problem while trying to rename type key from '" + typeName + "' to '" + newTypeName + "'. There is already an entry with key '" + newTypeName + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to rename type: " + e.getMessage(), e);
		}
	}

	public void removePropertyFromType(String typeName, String propertyToRemove) {
		try {
			String key = getTypeNameKey(typeName);
			this.jedis.hdel(key, propertyToRemove);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to rename type: " + e.getMessage(), e);
		}
	}
	
	public void renameTypeProperty(String typeName, String propertyNameOriginal, String propertyNameNew) {
		try {
			
			validatePropertyForRename1(propertyNameOriginal, propertyNameNew);
			
			String key = getTypeNameKey(typeName);
			Map<String, String> map = this.jedis.hgetAll(key);
			
			validatePropertyForRename2(map, propertyNameOriginal, propertyNameNew);
			
			String value = map.get(propertyNameOriginal);
			map.remove(propertyNameOriginal);
			map.put(propertyNameNew, value);
		
			this.removePropertyFromType(typeName, propertyNameOriginal);
			
			String result = this.jedis.hmset(key, map);
			if (!result.equals(Protocol.Keyword.OK.toString())) {
				throw new RuntimeException("Encountered problem while trying to rename type '" + typeName + "' property from '" + propertyNameOriginal + "' to '" + propertyNameNew + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to rename type: " + e.getMessage(), e);
		}
	}

	private void validatePropertyForRename1(String propertyNameOriginal, String propertyNameNew) {
		if (propertyNameOriginal.equals(DEFAULT_KEY_TRAN_DATE)
		|| propertyNameNew.equals(DEFAULT_KEY_TRAN_DATE)) {
			throw new RuntimeException("Encountered problem renaming property in type from '" + propertyNameOriginal + "' to '" + propertyNameNew + "'. Name cannot be reserved property name.");
		}
		
		if (null == propertyNameOriginal
		|| null == propertyNameNew) {
			propertyNameOriginal = (null == propertyNameOriginal) ? "<null>":  propertyNameOriginal;
			propertyNameNew = (null == propertyNameNew) ? "<null>":  propertyNameNew;
			throw new RuntimeException("Encountered problem renaming property in type from '" + propertyNameOriginal + "' to '" + propertyNameNew + "'. Neither can be null.");
		}
		
		if (propertyNameOriginal.equals(propertyNameNew)) {
			throw new RuntimeException("Encountered problem renaming property in type from '" + propertyNameOriginal + "' to '" + propertyNameNew + "'. The two values cannot be the same.");
		}
	}
	
	private void validatePropertyForRename2(Map<String, String> properties, String propertyNameOriginal, String propertyNameNew) {
		if (properties.containsKey(propertyNameNew)) {
			throw new RuntimeException("Encountered problem validating property rename from '" + propertyNameOriginal + "' to '" + propertyNameNew + "'. There already exista a field by that name.");
		}
	}
}
