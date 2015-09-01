package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.TranDateGenerator

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbTypeManagerLocalCacheSpec extends Specification {
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	OrbManager orbManager
	
	@Autowired
	Cache cache
	
	@Autowired
	CacheCloner cacheCloner
	
	@Autowired
	TranDateGenerator tranDateGenerator
	
	@Autowired
	CacheComponentComparator cacheComponentComparator
	
	def 'testDeleteOnlyOrb'() {
		
		given:
		def originalCount = orbTypeManager.getOrbTypeCount()
		
		def cloneOriginal = cacheCloner.clone(cache.cacheComponentsDto)
		
		OrbReference orbReference = cache.orbCollection.orbReference;
		
		LinkedHashSet<String> set = new LinkedHashSet<String>()
		set.add("bart")
		set.add("lisa")
		set.add("maggie")
		
		long orbTypeInternalId = orbTypeManager.createOrbType("foop-de-doop", set)
		
		createOrbs(1, orbTypeInternalId)
		
		when:
		orbTypeManager.deleteOrbType(orbTypeInternalId)
		
		then:
		orbTypeManager.getOrbTypeCount() == originalCount
		def cloneRecent = cacheCloner.clone(cache.cacheComponentsDto)
		def areSame = cacheComponentComparator.areSame(cloneOriginal, cloneRecent);
		areSame
		orbReference
	}
	
	def createOrbs(int numberOfOrbs, long orbTypeInternalId) {
		numberOfOrbs.times {
			Orb orb = orbManager.createOrb(orbTypeInternalId, tranDateGenerator.getTranDate())
			Assert.assertEquals(orb.userDefinedProperties.keySet().size(), 3, 0)
		}
	}

}
