package com.fletch22.redis;

import static org.junit.Assert.*

import org.junit.Test

import redis.clients.jedis.Jedis
import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.cache.external.NakedOrb;

class ObjectTypeCacheServiceSpec extends Specification {
	
	@Shared ObjectTypeCacheService objectTypeCacheService
	
	
	def setup() {
		this.objectTypeCacheService = new ObjectTypeCacheService()
		
		KeyGenerator keyGenerator = Mock()
		this.objectTypeCacheService.typeKeyGenerator = keyGenerator
		
		keyGenerator.getKeyPrefix() >> 'test'
		
		Jedis jedis = Mock()
		this.objectTypeCacheService.jedis = jedis
	}

	@Test
	def 'test delete all types'() {
		
		given:
		Map<String, String> map = new HashMap<String, String>()
		map.put('fooId1', 'fooBalue')
		
		this.objectTypeCacheService.jedis.hgetAll(*_) >> map
		
		Set<String> keys = new HashSet<String>()
		keys.add('key1')
		keys.add('key2')
		keys.add('key3')
		keys.add('key4')
		
		this.objectTypeCacheService.jedis.keys(*_) >> keys
		
		this.objectTypeCacheService.typeKeyGenerator.getKey(*_) >> 'testKey'
		
		this.objectTypeCacheService.jedis.del(*_) >> 1
		
		when:
		List<NakedOrb> deletedTypes = this.objectTypeCacheService.deleteAllTypes()
		
		then:
		notThrown(Exception)
		deletedTypes.size() == keys.size()
		
	}

}
