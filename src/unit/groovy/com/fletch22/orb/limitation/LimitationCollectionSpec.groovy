package com.fletch22.orb.limitation;

import static org.junit.Assert.*

import java.util.function.Predicate
import java.util.stream.Collectors

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.query.Criteria
import com.fletch22.util.StopWatch

class LimitationCollectionSpec extends Specification {
	
	@Shared Logger logger = LoggerFactory.getLogger(LimitationCollectionSpec)

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
		
		logger.debug("Elapsed millis: " + stopWatch.elapsedMillis)
		
		then:
		list.size() == 0
	}
	
	def 'test returning empty list'() {
	
		given:
		StopWatch stopWatch = new StopWatch()
		
		Map<Long, List<Criteria>> list = new HashMap<Long, List<Criteria>>()
		
		when:
		stopWatch.start()
		List<Criteria> isNullList = list.get(123l)
		isNullList = (isNullList == null) ? new ArrayList<Criteria>() : isNullList
		stopWatch.stop()
		println stopWatch.getElapsedMillis()
		
		def test = "test"
		
		then:
		test == "test"
		
	}
}