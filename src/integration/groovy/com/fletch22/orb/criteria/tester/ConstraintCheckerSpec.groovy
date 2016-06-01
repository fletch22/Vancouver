package com.fletch22.orb.criteria.tester;

import static com.googlecode.cqengine.query.QueryFactory.equal
import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.indexcollection.IndexedCollectionFactory
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.criteria.DefLimitationMother
import com.fletch22.orb.limitation.DefLimitationManager
import com.fletch22.orb.query.LogicalOperator
import com.fletch22.orb.query.constraint.Constraint
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.util.StopWatch

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class ConstraintCheckerSpec extends Specification {

	Logger logger = LoggerFactory.getLogger(ConstraintCheckerSpec)
	
	@Autowired
	ConstraintChecker constraintChecker

	@Autowired
	DefLimitationMother defCriteriaMother

	@Autowired
	OrbManager orbManager

	@Autowired
	Cache cache

	@Autowired
	IndexedCollectionFactory indexedCollectionFactory
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	DefLimitationManager defLimitationManager

	def testCriteriaSuccess() {

		given:
		
		Criteria criteria = defCriteriaMother.createAndAddCriteriaSimple();
		
		String expectedBarValue = "cat";

		long orbTypeInternalId = criteria.getOrbTypeInternalId();

		Orb orb = orbManager.createOrb(orbTypeInternalId);
		orb.getUserDefinedProperties().put(DefLimitationMother.ATTRIBUTE_BAR, expectedBarValue);
		
		LinkedHashSet<String> set = new LinkedHashSet<String>()
		set.add("banana");
		long orbTypeInternalId2 = orbTypeManager.createOrbType("fruitBowl", set)
		Orb orb2 = orbManager.createOrb(orbTypeInternalId2);
		orb2.getUserDefinedProperties().put("banana", "ripe");
		
		// NOTE: We're adding this later to avoid constraint violation on initial insert with empty attributes.
		// This would not be done in production. Done here for testing only.
		criteria.add(LogicalOperator.AND, Constraint.eq(DefLimitationMother.ATTRIBUTE_BAR, expectedBarValue))
		
		when:
		StopWatch stopWatch = new StopWatch()
		
		constraintChecker.getIndexedCollection()
		
		try {
			constraintChecker.checkConstraint(criteria, orb2)
		} catch (Exception e) {}
		finally {}
		
		stopWatch.start()
		constraintChecker.checkConstraint(criteria, orb)
		stopWatch.stop()

		then:
		criteria
		stopWatch.logElapsed()
	}
}
