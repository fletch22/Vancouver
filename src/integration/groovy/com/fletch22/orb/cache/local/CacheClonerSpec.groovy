package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Ignore
import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CacheClonerSpec extends Specification {
	
	@Autowired
	CacheCloner cacheCloner
	
	@Autowired
	Cache cache
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	CacheComponentsFactory cacheComponentsFactory
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	CacheComponentComparator cacheComponentComparator;

	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
	public void testSame() {
		
		given:
		CacheComponentsDto cacheComponentsDto = cache.getCacheComponentsDto()
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)
		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)

		when:
		def comparisonResult = cacheComponentComparator.areSame(cacheComponentsDtoOriginal, cacheComponentsDtoUpdated)
		
		then:
		comparisonResult.isSame
	}

	@Test
	public void testDifferentNumberOrbTypes() {
		
		given:
		CacheComponentsDto cacheComponentsDto = cache.getCacheComponentsDto()
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)
		orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)
		
		when:
		def comparisonResult = cacheComponentComparator.areSame(cacheComponentsDtoOriginal, cacheComponentsDtoUpdated)
		
		then:
		!comparisonResult.isSame
		comparisonResult.cacheDifferenceReasons == CacheDifferenceReasons.NUMBER_ORB_TYPES_DIFFERENT
	}
	
	@Test
	public void testNumberCustomFieldsInOrbType() {
		
		given:
		CacheComponentsDto cacheComponentsDto = cache.getCacheComponentsDto()
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		orbType.customFields.add("hotFire!")
		
		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)
		when:
		def comparisonResult = cacheComponentComparator.areSame(cacheComponentsDtoOriginal, cacheComponentsDtoUpdated)
		
		then:
		!comparisonResult.isSame
		comparisonResult.cacheDifferenceReasons == CacheDifferenceReasons.ORB_TYPE_CUSTOM_FIELDS_ARE_DIFFERENT_SIZES
	}
	
	@Test
	public void testModifiedCustomFieldOrbTypes() {
		
		given:
		CacheComponentsDto cacheComponentsDto = cache.getCacheComponentsDto()
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		orbType.customFields.add("fire")
		orbType.customFields.add("water")
		
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)

		orbType.customFields.remove("fire");
		orbType.customFields.add("earth")
		
		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)
		when:
		def comparisonResult = cacheComponentComparator.areSame(cacheComponentsDtoOriginal, cacheComponentsDtoUpdated)
		
		then:
		!comparisonResult.isSame
		comparisonResult.cacheDifferenceReasons == CacheDifferenceReasons.ORB_TYPE_CUSTOM_FIELD_DIFFERENT
	}
	
	
	// TODO: Needs to be finished.
	@Test
	@Ignore 
	public void testOrbComparisonSuccess() {
		given:
		when:
		def test = 1
		then:
		1 == 0
	}
}
