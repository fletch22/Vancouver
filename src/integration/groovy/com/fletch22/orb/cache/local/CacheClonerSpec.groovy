package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.InternalIdGenerator
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CacheClonerSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CacheClonerSpec)
	
	@Autowired
	CacheCloner cacheCloner
	
	@Autowired
	Cache cache
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	CacheComponentsFactory cacheComponentsFactory
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	CacheComponentComparator cacheComponentComparator;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;

	def setup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems()
	}
	
	def cleanup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems()
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
		when:
		
		CacheComponentsDto cacheComponentsDto = cache.getCacheComponentsDto()
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		orbType.customFields.add("hotFire!")
		
		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)
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
		
		OrbType orbType1 = orbTypeManager.getOrbType(orbTypeInternalId)
		orbType1.customFields.add("fire")
		orbType1.customFields.add("water")
		
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)

		orbType1.customFields.remove("fire")
		orbType1.customFields.add("earth")
		
		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)
		
		when:
		def comparisonResult = cacheComponentComparator.areSame(cacheComponentsDtoOriginal, cacheComponentsDtoUpdated)
		
		then:
		!comparisonResult.isSame
		comparisonResult.cacheDifferenceReasons == CacheDifferenceReasons.ORB_TYPE_CUSTOM_FIELD_DIFFERENT
	}
	
	@Test
	public void testModifiedCustomFieldOrbInstanceValue() {
		
		given:
		CacheComponentsDto cacheComponentsDto = cache.getCacheComponentsDto()
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		orbType.customFields.add("fire")
		orbType.customFields.add("water")
		
		Orb orb = orbManager.createOrb(orbType.id)
		orbManager.setAttributeNoCheckConstraints(orb.getOrbInternalId(), "fire", "hot");
		
		CacheComponentsDto cacheComponentsDtoOriginal = cacheCloner.clone(cacheComponentsDto)
		
		orbManager.setAttributeNoCheckConstraints(orb.getOrbInternalId(), "fire", "not-so-hot");

		CacheComponentsDto cacheComponentsDtoUpdated = cacheCloner.clone(cacheComponentsDto)
		when:
		def comparisonResult = cacheComponentComparator.areSame(cacheComponentsDtoOriginal, cacheComponentsDtoUpdated)
		
		then:
		!comparisonResult.isSame
		comparisonResult.cacheDifferenceReasons == CacheDifferenceReasons.ORB_PROPERTIES_VALUES_ARE_DIFFERENT
	}
}
