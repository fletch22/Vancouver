package com.fletch22.orb.limitation;

import static org.junit.Assert.*

import java.util.function.Predicate
import java.util.stream.Collectors

import spock.lang.Specification

import com.fletch22.orb.query.CriteriaImpl
import com.fletch22.util.StopWatch

class LimitationCollectionSpec extends Specification {

	def 'test lambda filter'() {
		
		given:
		LimitationCollection limitationCollection = new LimitationCollection();
		
		StopWatch stopWatch = new StopWatch()
		when:
		stopWatch.start()
		List<CriteriaImpl> list = limitationCollection.criteriaList.stream()
			.filter({CriteriaImpl criteria -> criteria.getOrbTypeInternalId() == 123} as Predicate<CriteriaImpl>)
			.collect(Collectors.toList())
		stopWatch.stop()
		
		println("Elapsed millis: " + stopWatch.elapsedMillis)
		
		then:
		list.size() == 0
	}
	
	def 'test returning empty list'() {
	
		given:
		StopWatch stopWatch = new StopWatch()
		
		Map<Long, List<CriteriaImpl>> list = new HashMap<Long, List<CriteriaImpl>>()
		
		when:
		stopWatch.start()
		List<CriteriaImpl> isNullList = list.get(123l)
		isNullList = (isNullList == null) ? new ArrayList<CriteriaImpl>() : isNullList
		stopWatch.stop()
		println stopWatch.getElapsedMillis()
		
		def test = "test"
		
		then:
		test == "test"
		
	}
}