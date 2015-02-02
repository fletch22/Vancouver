package com.fletch22.redis;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

@Component
public class ObjectTypeCacheService {
	
	private static final long DELETE_KEY_RESULT_KEY_NOT_FOUND = 0;
	private static final long RENAME_RESULT_KEY_ALREADY_EXISTS = 0;

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

	public void createType(String id, Map<String, String> properties) {
		
		try {
			validateTypeProperties(properties);

			String key = this.typeKeyGenerator.getKey(id);
			this.jedis.hmset(key, properties);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
	}
	
	private void validateTypeProperties(Map<String, String> properties) {
		if (null == properties) {
			throw new RuntimeException("Encountered problems validating the properties for a type. Properties were null. Should not be null.");
		}
		
		if (properties.size() == 0) {
			throw new RuntimeException("Encountered problems validating the properties for a type. There are zero properties. Properties should not be null.");
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
	
	public boolean removeType(String id) {
		boolean wasSuccessful = false;
		try {
			String key = getTypeIdKey(id);
			long result = jedis.del(getTypeIdKey(id));
			if (result == DELETE_KEY_RESULT_KEY_NOT_FOUND) {
				String message = String.format("Encountered problem with removing type. Could not find item with key '" + key + "' for type '" + id + "'. Because not found could not delete.");
				throw new RuntimeException(message);
			}
			wasSuccessful = true;
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return wasSuccessful;
	}
	
	private String getTypeIdKey(String id) {
		return this.typeKeyGenerator.getKey(id);
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

	public Map<String, String> getType(String id) {
		Map<String, String> result = null;
		try {
			String key = getTypeIdKey(id);
			result = this.jedis.hgetAll(key);
			if (!doesHashMapRecordExist(result)) {
				throw new RuntimeException("Encountered problem trying to get non-existent type '" + id + "' with key '" + key + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return result;
	}
	
	public boolean doesObjectTypeExist(String id) {
		boolean exists = false;
		try {
			String key = getTypeIdKey(id);
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
	
	public void reIdType(String id, String newId) {
		try {
			long result = this.jedis.renamenx(getTypeIdKey(id), getTypeIdKey(newId));
			if (result == RENAME_RESULT_KEY_ALREADY_EXISTS) {
				throw new RuntimeException("Encountered problem while trying to rename type key from '" + id + "' to '" + newId + "'. There is already an entry with key '" + newId + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to rename type: " + e.getMessage(), e);
		}
	}

	public void removePropertyFromType(String id, String propertyToRemove) {
		try {
			String key = getTypeIdKey(id);
			this.jedis.hdel(key, propertyToRemove);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to rename type: " + e.getMessage(), e);
		}
	}
	
	public void renameTypeProperty(String id, String propertyNameOriginal, String propertyNameNew) {
		try {
			
			validatePropertyForRename1(propertyNameOriginal, propertyNameNew);
			
			String key = getTypeIdKey(id);
			Map<String, String> map = this.jedis.hgetAll(key);
			
			validatePropertyForRename2(map, propertyNameOriginal, propertyNameNew);
			
			String value = map.get(propertyNameOriginal);
			map.remove(propertyNameOriginal);
			map.put(propertyNameNew, value);
		
			this.removePropertyFromType(id, propertyNameOriginal);
			
			String result = this.jedis.hmset(key, map);
			if (!result.equals(Protocol.Keyword.OK.toString())) {
				throw new RuntimeException("Encountered problem while trying to rename type '" + id + "' property from '" + propertyNameOriginal + "' to '" + propertyNameNew + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to rename type: " + e.getMessage(), e);
		}
	}

	private void validatePropertyForRename1(String propertyNameOriginal, String propertyNameNew) {
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
