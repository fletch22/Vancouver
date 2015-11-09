package com.fletch22.orb.cache.query;

import static org.junit.Assert.*
import spock.lang.Specification

import com.fletch22.orb.OrbType
import com.fletch22.orb.query.CriteriaFactory
import com.fletch22.orb.query.CriteriaFactory.Criteria

class CriteriaCollectionSpec extends Specification {

	def "test criteria validation success"() {
		
		given:
		CriteriaFactory criteriaFactory = new CriteriaFactory()
		CriteriaCollection criteriaCollection = new CriteriaCollection()
		
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = criteriaFactory.createInstance(orbType, 'first')
		criteriaCollection.add(234, criteriaOriginal)
		
		Criteria criteriaToValidate = criteriaFactory.createInstance(orbType, "shanks!")
		
		when:
		criteriaCollection.validateCriteria(id, criteriaToValidate)
		
		then:
		notThrown(Exception)
	}
	
	def "test criteria validation fail because dupe"() {
		
		given:
		CriteriaFactory criteriaFactory = new CriteriaFactory()
		CriteriaCollection criteriaCollection = new CriteriaCollection()
		
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = criteriaFactory.createInstance(orbType, 'first')
		criteriaCollection.add(234, criteriaOriginal)
		
		Criteria criteriaToValidate = criteriaFactory.createInstance(orbType, "first")
		
		when:
		criteriaCollection.validateCriteria(id, criteriaToValidate)
		
		then:
		thrown Exception
	}
	
	def "test criteria different orbs validation success"() {
		
		given:
		CriteriaFactory criteriaFactory = new CriteriaFactory()
		CriteriaCollection criteriaCollection = new CriteriaCollection()
		
		def id = 123l;
		
		OrbType orbType = new OrbType(123, "foo", 123, null);

		Criteria criteriaOriginal = criteriaFactory.createInstance(orbType, 'first')
		criteriaCollection.add(234, criteriaOriginal)
		
		OrbType orbType2 = new OrbType(234, "fooManChu", 345, null)
		Criteria criteriaToValidate = criteriaFactory.createInstance(orbType2, "first")
		
		when:
		criteriaCollection.validateCriteria(id, criteriaToValidate)
		
		then:
		notThrown(Exception)
	}

}
