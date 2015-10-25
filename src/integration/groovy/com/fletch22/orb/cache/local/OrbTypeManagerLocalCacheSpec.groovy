package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.junit.Assert
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.TranDateGenerator
import com.fletch22.orb.cache.reference.OrbReference;
import com.fletch22.orb.query.ConstraintDetailsSingleValue
import com.fletch22.orb.query.QueryManager
import com.fletch22.orb.query.CriteriaFactory.Criteria
import com.fletch22.orb.test.data.TestDataWithReferences

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbTypeManagerLocalCacheSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(OrbTypeManagerLocalCacheSpec)
	
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
	
	@Autowired
	TestDataWithReferences testDataWithReferences;
	
	private static final String ATTR_COLOR = "color"
	private static final String ATTR_SPEED = "speed"
	private static final String ATTR_FLAVOR = "flavor"
	
	@Shared
	Orb orbWithReference
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	QueryManager queryManager
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	def cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	def 'test delete type orb'() {
		
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
		orbTypeManager.deleteOrbType(orbTypeInternalId, true)
		
		then:
		orbTypeManager.getOrbTypeCount() == originalCount
		def cloneRecent = cacheCloner.clone(cache.cacheComponentsDto)
		def areSame = cacheComponentComparator.areSame(cloneOriginal, cloneRecent);
		areSame
		orbReference
	}
	
	def 'test delete attribute do not delete if has dependencies'() {
		
		given:
		def numInstances = 10
		Orb orbWithReference = testDataWithReferences.loadTestData(numInstances)
		long orbInternalIdQuery = testDataWithReferences.addSimpleCriteria()
		
		long orbTypeInternalId = orbWithReference.getOrbTypeInternalId()
		
		when:
		int indexOriginal = findIndexOfKey(orbTypeInternalId, ATTR_COLOR)
		orbTypeManager.deleteAttribute(orbTypeInternalId, ATTR_COLOR, false)
		
		then:
		thrown Exception
	}
	
	def 'test delete attribute and dependencies'() {
		
		given:
		def numInstances = 10
		Orb orbWithReference = testDataWithReferences.loadTestData(numInstances)
		long orbInternalIdQuery = testDataWithReferences.addSimpleCriteria()
		long orbTypeInternalId = orbWithReference.getOrbTypeInternalId()
		
		when:
		int indexOriginal = findIndexOfKey(orbTypeInternalId, TestDataWithReferences.ATTRIBUTE_COLOR)
		orbTypeManager.deleteAttribute(orbTypeInternalId, TestDataWithReferences.ATTRIBUTE_COLOR, true)
		
		logger.info("orbInternalIdQuery: {}", orbInternalIdQuery);
		
		then:
		notThrown Exception
		Criteria criteria = queryManager.get(orbInternalIdQuery);
		criteria == null
	}
	
	def 'test rename attribute'() {
		
		given:
		def numInstances = 10
		Orb orbWithReference = testDataWithReferences.loadTestData(numInstances)
		long orbInternalIdQuery = testDataWithReferences.addSimpleCriteria()
		
		long orbTypeInternalId = orbWithReference.getOrbTypeInternalId()
		
		int countOrig = cache.orbCollection.orbReference.referenceCollection.countArrowsPointingToTargetAttribute(orbWithReference.orbInternalId, ATTR_COLOR);
		assert countOrig == numInstances
		
		def attributeNameNew = "hue"
		
		when:
		int indexOriginal = findIndexOfKey(orbTypeInternalId, ATTR_COLOR)
		orbTypeManager.renameAttribute(orbTypeInternalId, ATTR_COLOR, attributeNameNew)
		int indexNew = findIndexOfKey(orbTypeInternalId, attributeNameNew)
		int countArrowsOld = cache.orbCollection.orbReference.referenceCollection.countArrowsPointingToTargetAttribute(orbWithReference.orbInternalId, ATTR_COLOR);
		int countArrowsNew = cache.orbCollection.orbReference.referenceCollection.countArrowsPointingToTargetAttribute(orbWithReference.orbInternalId, attributeNameNew);
		
		then:
		List<Orb> orbListAfterRename = orbManager.getOrbsOfType(orbTypeInternalId)
		
		for (Orb orb : orbListAfterRename) {
			assertTrue(orb.userDefinedProperties.containsKey(attributeNameNew))
			assertFalse(orb.userDefinedProperties.containsKey(ATTR_COLOR))
		}
		
		assertEquals("Index order of attribute should be same.", indexOriginal, indexNew)
		assert countArrowsOld == 0
		assert countArrowsNew == numInstances
		
		Criteria criteria = queryManager.get(orbInternalIdQuery)
		criteria
		ConstraintDetailsSingleValue constraintDetailSingleValue = (ConstraintDetailsSingleValue) criteria.logicalConstraint.constraintList.get(0)
		constraintDetailSingleValue
		constraintDetailSingleValue.attributeName == attributeNameNew
	}
	
	public int findIndexOfKey(long orbTypeInternalId, String key) {
		
		List<Orb> orbList = orbManager.getOrbsOfType(orbTypeInternalId)
		
		Set<String> keys = orbList.get(0).userDefinedProperties.keySet()
		
		int i = 0
		boolean wasKeyFound = false
		for (String keyFound: keys) {
			if (keyFound.equals(key)) {
				wasKeyFound = true;
				break;
			}
		}
		
		if (!wasKeyFound) {
			throw new RuntimeException("Could not find index of key.")
		}
		
		return i
	}
	
	def createOrbs(int numberOfOrbs, long orbTypeInternalId) {
		numberOfOrbs.times {
			Orb orb = orbManager.createOrb(orbTypeInternalId)
			Assert.assertEquals(orb.userDefinedProperties.keySet().size(), 3, 0)
		}
	}
	
}
