package com.fletch22.orb.limitation;

import static org.junit.Assert.*

import java.util.function.Predicate
import java.util.stream.Collectors

import spock.lang.Specification

import com.fletch22.orb.query.CriteriaFactory.Criteria
import com.fletch22.util.StopWatch

class LimitationCollectionSpec extends Specification {

	def 'test lambda filter'() {
		
		given:
		LimitationCollection limitationCollection = new LimitationCollection();
		
		StopWatch stopWatch = new StopWatch()
		when:
		stopWatch.start()
		List<Criteria> list = limitationCollection.criteriaList.stream()
			.filter({Criteria criteria -> criteria.getOrbTypeInternalId() == 123} as Predicate<Criteria>)
			.collect(Collectors.toList())
		stopWatch.stop()
		
		println("Elapsed millis: " + stopWatch.elapsedMillis)
		
		then:
		list.size() == 0
		
	}

}
