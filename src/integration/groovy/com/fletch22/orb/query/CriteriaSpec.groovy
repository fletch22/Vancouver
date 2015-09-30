package com.fletch22.orb.query;

import static org.junit.Assert.*

import org.apache.commons.lang3.time.StopWatch
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
import com.fletch22.orb.cache.local.CacheEntry
import com.fletch22.orb.cache.local.OrbSingleTypesInstanceCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class CriteriaSpec extends Specification {

	Logger logger = LoggerFactory.getLogger(CriteriaSpec)
	
	@Autowired
	Cache cache

	@Autowired
	CriteriaFactory criteriaFactory

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
		Criteria criteria = criteriaFactory.createInstance(orbType, "foo")

		criteria.addAnd(Constraint.eq(ATTRIBUTE_COLOR, COLOR_TO_FIND))

		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = cache.orbCollection.allInstances.get(criteria.getOrbTypeInternalId());
		
		ConstraintGrinder criteriaGrinder = new ConstraintGrinder(criteria, orbSingleTypesInstanceCollection.instances);
		
		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		List<Orb> orbList = criteriaGrinder.list();
		stopWatch.stop()
		
		def elapsed = new BigDecimal(stopWatch.nanoTime).divide(new BigDecimal(1000000))
		logger.info("elapsed time: {}", elapsed)
		
		then:
		notThrown Exception
		orbList.size() > 0
	}
	
	def 'test criteria search or'() {
		
		given:
		Criteria criteria = criteriaFactory.createInstance(orbType, "foo")

		criteria.addOr(Constraint.eq(ATTRIBUTE_COLOR, "red"), Constraint.eq(ATTRIBUTE_COLOR, COLOR_TO_FIND), Constraint.eq(ATTRIBUTE_COLOR, "orange"))

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		List<Orb> orbList = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		def elapsed = new BigDecimal(stopWatch.nanoTime).divide(new BigDecimal(1000000))
		logger.info("elapsed time: {}", elapsed)
		
		then:
		notThrown Exception
		orbList
		orbList.size == 110
	}
	
	def 'test criteria search collection'() {
		
		given:
		Criteria criteria = criteriaFactory.createInstance(orbType, "foo")
		
		Constraint[] constraintArray = new Constraint[3]
		constraintArray[0] = Constraint.eq(ATTRIBUTE_COLOR, "red")
		constraintArray[1] = Constraint.eq(ATTRIBUTE_COLOR, COLOR_TO_FIND)
		constraintArray[2] = Constraint.eq(ATTRIBUTE_COLOR, "orange")
		
		criteria.addOr(constraintArray)

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		List<Orb> orbList = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		def elapsed = new BigDecimal(stopWatch.nanoTime).divide(new BigDecimal(1000000))
		logger.info("elapsed time: {}", elapsed)
		
		then:
		notThrown Exception
		orbList
		orbList.size == 110
	}
	
	def 'test criteria search collection using in'() {
		
		given:
		Criteria criteria = criteriaFactory.createInstance(orbType, "foo")
		
		List<String> list = new ArrayList<String>();
		list.add("red")
		list.add("orange")
		
		criteria.addAnd(Constraint.in(ATTRIBUTE_COLOR, list))

		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		List<Orb> orbList = cache.orbCollection.executeQuery(criteria);
		stopWatch.stop()
		
		def elapsed = new BigDecimal(stopWatch.nanoTime).divide(new BigDecimal(1000000))
		logger.info("elapsed time: {}", elapsed)
		
		then:
		notThrown Exception
		orbList
		orbList.size == 70
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
