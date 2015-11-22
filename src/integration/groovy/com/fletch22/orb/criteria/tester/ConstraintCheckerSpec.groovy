package com.fletch22.orb.criteria.tester;

import static com.googlecode.cqengine.query.QueryFactory.equal
import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.indexcollection.IndexedCollectionFactory
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.criteria.CriteriaMother
import com.fletch22.orb.query.Constraint
import com.fletch22.orb.query.LogicalOperator
import com.fletch22.orb.query.CriteriaFactory.Criteria
import com.fletch22.util.StopWatch

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class ConstraintCheckerSpec extends Specification {

	@Autowired
	ConstraintChecker constraintChecker

	@Autowired
	CriteriaMother criteriaMother

	@Autowired
	OrbManager orbManager

	@Autowired
	Cache cache

	@Autowired
	IndexedCollectionFactory indexedCollectionFactory
	
	@Autowired
	OrbTypeManager orbTypeManager

	def testCriteriaSuccess() {

		given:
		Criteria criteria = criteriaMother.getCriteriaSample();
		
		String expectedBarValue = "cat";

		criteria.add(LogicalOperator.AND, Constraint.eq(CriteriaMother.ATTRIBUTE_BAR, expectedBarValue))

		long orbTypeInternalId = criteria.getOrbTypeInternalId();

		Orb orb = orbManager.createOrb(orbTypeInternalId);
		orb.getUserDefinedProperties().put(CriteriaMother.ATTRIBUTE_BAR, expectedBarValue);
		
		LinkedHashSet<String> set = new LinkedHashSet<String>()
		set.add("banana");
		long orbTypeInternalId2 = orbTypeManager.createOrbType("fruitBowl", set)
		Orb orb2 = orbManager.createOrb(orbTypeInternalId2);
		orb2.getUserDefinedProperties().put("banana", "ripe");

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
		println stopWatch.elapsedMillis
	}
}
