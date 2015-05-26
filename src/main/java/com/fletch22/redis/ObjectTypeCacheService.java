package com.fletch22.redis;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import com.fletch22.orb.NakedOrb;

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

	public void createType(NakedOrb nakedOrb) {
		
		Map<String, String> properties = nakedOrb.expressAllProperties();
		
		try {
			validateTypeProperties(properties);

			String key = this.typeKeyGenerator.getKey(nakedOrb.getOrbInternalId());
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
	
	public NakedOrb deleteType(String id) {
		
		NakedOrb deletedOrb = null;
		try {
			
			String key = getTypeIdKey(id);
			deletedOrb = new NakedOrb(getType(id));
			long result = this.jedis.del(getTypeIdKey(id));
			if (result == DELETE_KEY_RESULT_KEY_NOT_FOUND) {
				String message = String.format("Encountered problem with removing type. Could not find item with key '" + key + "' for type '" + id + "'. Because not found could not delete.");
				throw new RuntimeException(message);
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return deletedOrb;
	}
	
	public List<NakedOrb> deleteAllTypes() {
		
		List<NakedOrb> deletedOrbs = new ArrayList<NakedOrb>();
		try {
			Set<String> keyList = this.getTypes();
			
			for (String key: keyList) {
				NakedOrb nakedOrb = new NakedOrb(getTypeFromKey(key));
				NakedOrb deletedOrb = this.deleteType(nakedOrb.getOrbInternalId());
				deletedOrbs.add(deletedOrb);
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return deletedOrbs;
	}
	
	private String getTypeIdKey(String id) {
		return this.typeKeyGenerator.getKey(id);
	}

	// TODO: 06-01-2015: Should be moved into general cache utility.
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
			result = getTypeFromKey(key);
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem while trying to create type: " + e.getMessage(), e);
		}
		return result;
	}

	private Map<String, String> getTypeFromKey(String key) {
		
		Map<String, String> result;
		result = this.jedis.hgetAll(key);
		if (!doesHashMapRecordExist(result)) {
			throw new RuntimeException("Encountered problem trying to get non-existent type '" + key + "'.");
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
