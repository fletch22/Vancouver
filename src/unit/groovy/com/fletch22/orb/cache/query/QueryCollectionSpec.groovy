package com.fletch22.orb.cache.query;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.OrbType
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaStandard;

public class QueryCollectionSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(QueryCollectionSpec)
	
	@Shared CriteriaCollection queryCollection = new QueryCollection()
	
	def cleanup() {
		queryCollection.clear()
	}

	def "test Criteria Validate Success"() {
		
		given:
		def id = 123l;
		
		long orbTypeInternalId = 123555
		OrbType orbType = new OrbType(orbTypeInternalId, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		queryCollection.put(criteriaOriginal)
		
		Criteria criteriaToValidate = new CriteriaStandard(orbType.id, "shanks!")
		criteriaToValidate.setId(444)
		
		when:
		queryCollection.validateCriteria(criteriaToValidate)
		
		then:
		notThrown(Exception)
		queryCollection.criteriaByOrbTypeCollection.collection.get(orbTypeInternalId).size() == 1
	}
	
	def "test criteria validation fail because dupe"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		queryCollection.put(criteriaOriginal)
		
		Criteria criteriaToValidate = new CriteriaStandard(orbType.id, "first")
		criteriaToValidate.setId(444)
		
		when:
		queryCollection.validateCriteria(criteriaToValidate)
		
		then:
		thrown Exception
	}
	
	def "test criteria different orbs validation success"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		queryCollection.put(criteriaOriginal)
		
		OrbType orbType2 = new OrbType(234, "fooManChu", 345, null)
		Criteria criteriaToValidate = new CriteriaStandard(orbType2.id, "first")
		criteriaToValidate.setId(444)
		
		when:
		queryCollection.validateCriteria(criteriaToValidate)
		
		then:
		notThrown(Exception)
	}
	
	def "test criteria creation in both collections"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		
		when:
		queryCollection.put(criteriaOriginal)
				
		then:
		queryCollection.getByOrbTypeInsideCriteria(123) != null
		queryCollection.getByQueryId(criteriaOriginal.getCriteriaId()) != null
	}
	
	def "test criteria removal by query ID removes in both collections"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		
		queryCollection.put(criteriaOriginal)
		
		Set<Long> keySet = queryCollection.criteriaByOrbTypeCollection.collection.keySet()
		assertEquals 1, keySet.size()
		
		when:
		Criteria criteria = queryCollection.removeByCriteriaId(criteriaOriginal.getCriteriaId())
		
		then:
		criteria.is(criteriaOriginal)
		queryCollection.criteriaByIdMap.size() == 0
		queryCollection.criteriaByOrbTypeCollection.collection.size() == 0
		
		Set<Long> keySet2 = queryCollection.criteriaByOrbTypeCollection.collection.keySet()
		assertEquals 0, keySet2.size()
	}
	
	def "test criteria removal by orb type ID removes in both collections"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		queryCollection.put(criteriaOriginal)
		
		Set<Long> keySet = queryCollection.criteriaByOrbTypeCollection.collection.keySet()
		assertEquals 1, keySet.size()
		
		when:
		HashMap<Long, Criteria> criteriaMap = queryCollection.removeByOrbTypeId(criteriaOriginal.getOrbTypeInternalId())
		
		then:
		criteriaMap.get(criteriaOriginal.getCriteriaId()).is(criteriaOriginal)
		queryCollection.criteriaByIdMap.size() == 0
		queryCollection.criteriaByOrbTypeCollection.collection.size() == 0
		
		Set<Long> keySet2 = queryCollection.criteriaByOrbTypeCollection.collection.keySet()
		assertEquals 0, keySet2.size()
	}
	
	def "test criteria clear"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		
		queryCollection.put(criteriaOriginal)
		
		Set<Long> keySet = queryCollection.criteriaByOrbTypeCollection.collection.keySet()
		assertEquals 1, keySet.size()
		
		when:
		Criteria criteria = queryCollection.clear()
		
		then:
		queryCollection.criteriaByIdMap.size() == 0
		queryCollection.criteriaByOrbTypeCollection.collection.size() == 0
		
		Set<Long> keySet2 = queryCollection.criteriaByOrbTypeCollection.collection.keySet()
		assertEquals 0, keySet2.size()
	}
	
	def "test does criteria with Orb Type internal ID exist"() {
		
		given:
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = createSampleCriteria(orbType)
		
		queryCollection.put(criteriaOriginal)
		
		when:
		boolean doesExist = queryCollection.doesCriteriaExistWithOrbTypeInternalId(orbType.id);
		
		then:
		doesExist
		queryCollection.clear()
		boolean doesExistNow = queryCollection.doesCriteriaExistWithOrbTypeInternalId(orbType.id);
		!doesExistNow
	}
	
	private Criteria createSampleCriteria(OrbType orbType) {
		Criteria criteriaOriginal = new CriteriaStandard(orbType.id, 'first')
		criteriaOriginal.setId(234)
		return criteriaOriginal
	}
}
