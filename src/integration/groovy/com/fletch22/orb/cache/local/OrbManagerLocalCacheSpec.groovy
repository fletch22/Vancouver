package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.StopWatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.TranDateGenerator
import com.fletch22.orb.cache.reference.OrbReference
import com.fletch22.orb.cache.reference.ReferenceCollection
import com.fletch22.orb.cache.reference.ReferenceUtil
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.command.transaction.RollbackTransactionService;
import com.fletch22.util.RandomUtil

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbManagerLocalCacheSpec extends Specification {

	Logger logger = LoggerFactory.getLogger(OrbManagerLocalCacheSpec)

	@Autowired
	OrbTypeManager orbTypeManager

	@Autowired
	OrbManager orbManager

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer

	@Autowired
	RandomUtil randomUtil

	@Autowired
	OrbReference orbReference
	
	@Autowired
	TranDateGenerator tranDateGenerator
	
	@Autowired
	Cache cache
	
	@Autowired
	CacheCloner cacheCloner
	
	@Autowired
	BeginTransactionService beginTransactionService
	
	@Autowired
	RollbackTransactionService rollbackTransactionService
	
	@Autowired
	CacheComponentComparator cacheComponentComparator
	
	@Autowired
	ReferenceUtil referenceUtil

	def setup()  {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	def tearDown() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	def 'testAddIllegalAttributeValue'() {

		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foop", new LinkedHashSet<String>())

		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName)

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		String tranDateString = orbType.tranDate.toString()
		BigDecimal tranDate = new BigDecimal(tranDateString)

		Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)

		when:
		orbManager.setAttribute(orb.getOrbInternalId(), attributeName, ReferenceCollection.REFERENCE_KEY_PREFIX)

		then:
		final Exception exception = thrown()
	}

	def 'test delete instance no references'() {

		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foop", new LinkedHashSet<String>())

		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName)

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		String tranDateString = orbType.tranDate.toString()
		BigDecimal tranDate = new BigDecimal(tranDateString)

		Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)

		orbManager.setAttribute(orb.getOrbInternalId(), attributeName, "bar")

		when:
		orbManager.deleteOrb(orb.orbInternalId, true)

		then:
		!orbManager.doesOrbExist(orb.orbInternalId);
	}
	
	def 'test delete instance with references'() {

		given:
		def tranDate = beginTransactionService.beginTransaction()
		
		def original = cacheCloner.clone(cache.cacheComponentsDto)
		
		long orbTypeInternalId = orbTypeManager.createOrbType("foop", new LinkedHashSet<String>())

		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName)

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		String referenceValue = createReferences(orbType, 100)

		Orb orb = orbManager.createOrb(orbTypeInternalId, tranDateGenerator.getTranDate())
		orbManager.setAttribute(orb.getOrbInternalId(), attributeName, referenceValue)
		
		orbManager.deleteOrb(orb.orbInternalId, true)
		
		rollbackTransactionService.rollbackToSpecificTransaction(tranDate)
		
		when:
		def rolledBack = cacheCloner.clone(cache.cacheComponentsDto)

		then:
		cacheComponentComparator.areSame(original, rolledBack)
	}
	
	private List<String> createReferences(OrbType orbType, int numberOfReference) {
		
		List<String> referenceList = []
		numberOfReference.times {
		Orb orb = orbManager.createOrb(orbType.id, tranDateGenerator.getTranDate())
		orbManager.setAttribute(orb.orbInternalId, "foo", "bar")
			referenceList << referenceUtil.composeReference(orb.orbInternalId, "foo")
		}
		
		return referenceList
	}

	def 'test Set Reference Attribute'() {

		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foop", new LinkedHashSet<String>())

		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName)

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		String tranDateString = orbType.tranDate.toString()
		BigDecimal tranDate = new BigDecimal(tranDateString)
		
		Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)

		StopWatch stopWatch = new StopWatch()
 
		stopWatch.start()
		def numberOfReferences = 100
		def set1 = getSet(numberOfReferences)
		def set2 = getSet(numberOfReferences)
		stopWatch.stop()
		
		def elapsedMillis = new BigDecimal(stopWatch.getNanoTime()).divide(1000000)
		
		logger.info("Time to get 2 * 2 * {} orbs/orb types: {}", numberOfReferences, elapsedMillis)
		
		stopWatch.reset()

		def numberSetActions = 100

		stopWatch.start()
		def index = 0
		numberSetActions.times {
			index++
			def setToUse = index % 2 > 0 ? set1: set2 
			
			orbManager.setAttribute(orb.getOrbInternalId(), attributeName, setToUse)
		}
		stopWatch.stop()
		
		Thread.sleep(10000);

		def elapsedMillisPerSetAction = new BigDecimal(stopWatch.getNanoTime()).divide(1000000).divide(numberSetActions)

		when:
		logger.info("Each set action averaged {} millis", elapsedMillisPerSetAction)
		def fetchedValue = orbManager.getAttribute(orb.orbInternalId, attributeName)

		then:
		fetchedValue == set1 ? true: fetchedValue == set2 ? true: false 
	}
	
	def 'test add reference'() {
		
		given:
		String attributeNameArrow = 'foo'
		String attributeNameTarget = 'banana'
		
		Set<String> customFields = new LinkedHashSet<String>()
		customFields.add(attributeNameArrow)
		customFields.add(attributeNameTarget)
		long orbInternalIdType = orbTypeManager.createOrbType(attributeNameArrow, customFields)
		
		Orb orbTarget = orbManager.createOrb(orbInternalIdType)
		String targetRef = referenceUtil.composeReference(orbTarget.orbInternalId, 'foo')
		
		Orb orbArrow = orbManager.createOrb(orbInternalIdType)
		orbManager.setAttribute(orbArrow.getOrbInternalId(), attributeNameArrow, targetRef)
		
		when:
		OrbManagerLocalCache orbManagerLocalCache = (OrbManagerLocalCache) orbManager
		orbManagerLocalCache.addReference(orbArrow.orbInternalId, attributeNameArrow, orbTarget.orbInternalId, attributeNameTarget)
				
		int countArrows = orbManagerLocalCache.cache.orbCollection.orbReference.referenceCollection.countArrowsPointingToTargetAttribute(orbTarget.orbInternalId, attributeNameTarget)
		int countTotalArrows = orbManagerLocalCache.cache.orbCollection.orbReference.referenceCollection.countArrows()
		
		then:
		countArrows == 1
		
		countTotalArrows == 2
	}
	
	private String getSet(int numberOfReferences) {
		Set<String> set = new HashSet<String>()
		numberOfReferences.times {
			def orbInternalId = randomUtil.getRandom(1, 1000)
			def attributeName = randomUtil.getRandomString(10)
			
			LinkedHashSet<String> fieldSet = new LinkedHashSet<String>()
			fieldSet.add(attributeName)
			
			def orbTypeInternalId = orbTypeManager.createOrbType(attributeName, fieldSet)
			
			def tranDate = tranDateGenerator.getTranDate()
			Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)
			
			def composedKey = referenceUtil.composeReference(orb.getOrbInternalId(), attributeName)
			set.add(composedKey)
		}

		return StringUtils.join(set, ',');
	}
}
