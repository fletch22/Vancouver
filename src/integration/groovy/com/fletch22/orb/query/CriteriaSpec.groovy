package com.fletch22.orb.query;

import static org.junit.Assert.*

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
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.cache.local.OrbSingleTypesInstanceCollectionFactory.OrbSingleTypesInstanceCollection
import com.fletch22.orb.cache.query.QueryCollection
import com.fletch22.orb.query.Criteria
import com.fletch22.orb.query.constraint.Constraint
import com.fletch22.orb.query.constraint.ConstraintGrinder
import com.fletch22.orb.query.constraint.aggregate.Aggregate
import com.fletch22.util.StopWatch

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class CriteriaSpec extends Specification {

	Logger logger = LoggerFactory.getLogger(CriteriaSpec)
	
	@Autowired
	Cache cache

	@Autowired
	OrbManager orbManager

	@Autowired
	OrbTypeManager orbTypeManager

	@Shared
	static final String ATTRIBUTE_COLOR = 'color'

	@Shared
	static final String COLOR_TO_FIND = 'green'

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Shared
	def orbType

	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
		def orbTypeInternalId = loadTestData()
		orbType = orbTypeManager.getOrbType(orbTypeInternalId)
	}

	def cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	def 'test criteria search for green'() {

		given:
		Criteria criteria = new CriteriaStandard(orbType.id, "foo")

		criteria.addAnd(Constraint.eq(ATTRIBUTE_COLOR, COLOR_TO_FIND))

		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = cache.orbCollection.allInstances.get(criteria.getOrbTypeInternalId());
		
		ConstraintGrinder criteriaGrinder = new ConstraintGrinder(criteria, orbSingleTypesInstanceCollection.instances);
		
		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = criteriaGrinder.list();
		stopWatch.stop()
		
		logger.debug("elapsed time: {}", stopWatch.elapsedMillis)
		
		then:
		notThrown Exception
		orbResultSet.orbList.size() > 0
	}
	
	def 'test criteria search or'() {
		
		given:
		Criteria criteria = new CriteriaStandard(orbType.id, "foo")

		criteria.addOr(Constraint.eq(ATTRIBUTE_COLOR, "red"), Constraint.eq(ATTRIBUTE_COLOR, COLOR_TO_FIND), Constraint.eq(ATTRIBUTE_COLOR, "orange"))

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		logger.debug("elapsed time: {}", stopWatch.elapsedMillis)
		
		then:
		notThrown Exception
		orbResultSet.orbList
		orbResultSet.orbList.size == 110
	}
	
	def 'test criteria search collection'() {
		
		given:
		Criteria criteria = new CriteriaStandard(orbType.id, "foo")
		
		Constraint[] constraintArray = new Constraint[3]
		constraintArray[0] = Constraint.eq(ATTRIBUTE_COLOR, "red")
		constraintArray[1] = Constraint.eq(ATTRIBUTE_COLOR, COLOR_TO_FIND)
		constraintArray[2] = Constraint.eq(ATTRIBUTE_COLOR, "orange")
		
		criteria.addOr(constraintArray)

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		logger.debug("elapsed time: {}", stopWatch.elapsedMillis)
		
		then:
		notThrown Exception
		orbResultSet
		orbResultSet.orbList.size == 110
	}
	
	def 'test criteria search collection using in'() {
		
		given:
		Criteria criteria = new CriteriaStandard(orbType.id, "foo")
		
		List<String> list = new ArrayList<String>();
		list.add("red")
		list.add("orange")
		
		criteria.addAnd(Constraint.in(ATTRIBUTE_COLOR, list))

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		logger.debug("elapsed time: {}", stopWatch.elapsedMillis)
		
		then:
		notThrown Exception
		orbResultSet.orbList
		orbResultSet.orbList.size == 70
	}
	
	def 'test criteria search collection using in - when only one item.'() {
		
		given:
		Criteria criteria = new CriteriaStandard(orbType.id, "foo")
		
		List<String> list = new ArrayList<String>();
		list.add("red")
		
		criteria.addAnd(Constraint.in(ATTRIBUTE_COLOR, list))

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		logger.debug("elapsed time: {}", stopWatch.elapsedMillis)
		
		then:
		notThrown Exception
		orbResultSet.orbList
		orbResultSet.orbList.size == 60
	}
	
	def 'test criteria search collection using is unique and parent set'() {
		
		given:
		Criteria criteria = new CriteriaStandard(orbType.id, "foo")
		
		CriteriaAggregate criteriaForAggregation = new CriteriaAggregate(orbType.id, "asdfdsaf", ATTRIBUTE_COLOR)
		criteria.addAnd(Constraint.is(ATTRIBUTE_COLOR, Aggregate.AMONGST_UNIQUE, criteriaForAggregation))
		
		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		then:
		notThrown Exception
		orbResultSet.orbList
		orbResultSet.orbList.size == 1
	}

	public long loadTestData() {

		LinkedHashSet<String> customFields = new LinkedHashSet<String>()

		customFields.add(ATTRIBUTE_COLOR)
		customFields.add("size")
		customFields.add("speed")

		def orbTypeInternalId = orbTypeManager.createOrbType('foo', customFields)

		def color = 'red'
		
		def numInstances = 60
		setNumberInstancesToColor(60, orbTypeInternalId, "red")
		setNumberInstancesToColor(10, orbTypeInternalId, "orange")
		setNumberInstancesToColor(1, orbTypeInternalId, "puce")
		setNumberInstancesToColor(40, orbTypeInternalId, COLOR_TO_FIND)
		
		return orbTypeInternalId
	}

	private setNumberInstancesToColor(int numInstances, long orbTypeInternalId, color) {
		numInstances.times {
			Orb orb = orbManager.createOrb(orbTypeInternalId)
			orbManager.setAttribute(orb.orbInternalId, ATTRIBUTE_COLOR, color);
		}
	}
}
