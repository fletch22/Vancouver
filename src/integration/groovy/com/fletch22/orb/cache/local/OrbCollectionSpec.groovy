package com.fletch22.orb.cache.local;

import static org.junit.Assert.assertTrue

import java.util.List;

import junit.framework.Assert

import org.apache.commons.lang3.time.StopWatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.InternalIdGenerator
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.TranDateGenerator
import com.fletch22.orb.cache.local.OrbCollection.OrbSteamerTrunk
import com.fletch22.orb.cache.reference.DecomposedKey;
import com.fletch22.orb.cache.reference.OrbReference
import com.fletch22.orb.cache.reference.ReferenceUtil
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.command.orbType.dto.AddOrbDto
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
import com.fletch22.orb.command.transaction.RollbackTransactionService
import com.fletch22.orb.query.QueryManager
import com.fletch22.orb.query.OrbResultSet
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.sort.CriteriaSortInfo
import com.fletch22.orb.query.sort.SortInfo.SortDirection
import com.fletch22.orb.rollback.UndoActionBundle
import com.fletch22.orb.test.data.TestDataSimple


@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbCollectionSpec extends Specification {

	@Shared Logger logger = LoggerFactory.getLogger(OrbCollectionSpec.class)

	@Autowired
	OrbTypeManager orbTypeManager

	@Autowired
	OrbManager orbManager

	@Autowired
	Cache cache

	OrbCollection orbCollection

	OrbTypeCollection orbTypeCollection
	
	@Autowired
	TranDateGenerator tranDateGenerator
	
	@Autowired
	InternalIdGenerator internalIdGenerator
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	BeginTransactionService beginTransactionService
	
	@Autowired
	RollbackTransactionService rollbackTransactionService
	
	@Autowired
	TestDataSimple testDataSimple
	
	@Autowired
	QueryManager queryManager
	
	@Autowired
	ReferenceUtil referenceUtil
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
		orbCollection = cache.orbCollection;
		orbTypeCollection = cache.orbTypeCollection;
	}
	
	def cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def 'testQuery'() {
		
		given:
		testDataSimple.loadTestData()
		long orbInternalIdQuery = testDataSimple.addSimpleCriteria()
		
		Criteria criteria = queryManager.get(orbInternalIdQuery)
		List<CriteriaSortInfo> criteriaSortInfoList = criteria.getSortInfoList()
		
		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo()
		criteriaSortInfo.sortDirection = SortDirection.DESC
		criteriaSortInfoList.add(criteriaSortInfo)
		criteriaSortInfo.sortAttributeName = TestDataSimple.ATTRIBUTE_COLOR
		
		logger.debug("Criteria null? {}", criteria == null)
		
		when:
		StopWatch stopWatch = new StopWatch()
		stopWatch.start()
		OrbResultSet orbResultSet = cache.orbCollection.executeQuery(criteria)
		stopWatch.stop()
		
		BigDecimal millis = new BigDecimal(stopWatch.nanoTime).divide(new BigDecimal(1000000))
		
		logger.debug("Elapsed query time: {}", millis)
		
		then:
		orbResultSet.getOrbList().size > 0
		orbResultSet.getOrbList().size == 40
	}

	def testSuccess() {

		// Arrange
		given:
		long orbTypeInternalId = createOrbType()

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		long totalTime = 0
		int maxCount = 1
		for (int i in (1..maxCount))  {
			AddOrbDto addOrbDto = new AddOrbDto()
			addOrbDto.orbTypeInternalId = orbTypeInternalId

			String tranDateString = orbType.tranDate.toString() + i.toString()
			BigDecimal tranDate = new BigDecimal(tranDateString)

			UndoActionBundle undoActionBundle = new UndoActionBundle()

			orbManager.createOrb(addOrbDto, tranDate, undoActionBundle)
		}

		when:
		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName);

		then:
		orbTypeInternalId != null
		orbType != null
	}
	
	def testRemoveAttributeFromType() {
		
		given:
		
		long orbTypeInternalId = createOrbType()
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		def attributeName = 'foo'
		orbTypeManager.addAttribute(orbType.id, attributeName)
		Orb orbToTarget = orbManager.createOrb(orbTypeInternalId)
		def numberOrbs = 1
		OrbReference orbReference = cache.orbCollection.orbReference;
		
		numberOrbs.times {
			Orb orb = orbManager.createOrb(orbTypeInternalId)
			def referenceValue = referenceUtil.composeReference(orbToTarget.getOrbInternalId(), "foo");
			orbManager.setAttribute(orb.getOrbInternalId(), attributeName, referenceValue);
		}
		
		Assert.assertEquals numberOrbs, orbReference.@referenceCollection.countArrows()
		 
		Map<Long, OrbSteamerTrunk> map = orbCollection.getQuickLookup()
		assertOrbPropertySize(map, 1)
		
		def tranId = beginTransactionService.beginTransaction()
		orbTypeManager.deleteAttribute(orbTypeInternalId, attributeName, true)
		map.size() == numberOrbs
		assertOrbPropertySize(map, 0)
		
		Assert.assertEquals 0, orbReference.@referenceCollection.countArrows()
		
		when:
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId)
		
		then:
		assertOrbPropertySize(map, 1)
		logger.debug("Arrows: {}", orbReference.@referenceCollection.countArrows())
		orbReference.@referenceCollection.countArrows() == numberOrbs
	}
	
	def "disallowReferenceOfReference"() {
		
		given:
		long orbTypeInternalId = createOrbType()
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		def attributeName = 'foo'
		orbTypeManager.addAttribute(orbType.id, attributeName)
		
		Orb orb1 = orbManager.createOrb(orbTypeInternalId)
		
		OrbReference orbReference = cache.orbCollection.orbReference
		
		Orb orb2 = orbManager.createOrb(orbTypeInternalId)
		def referenceValue = referenceUtil.composeReference(orb1.getOrbInternalId(), "foo")
		orbManager.setAttribute(orb2.getOrbInternalId(), attributeName, referenceValue)

		Orb orb3 = orbManager.createOrb(orbTypeInternalId)
		referenceValue = referenceUtil.composeReference(orb2.getOrbInternalId(), "foo")
		
		when:
		orbManager.setAttribute(orb1.getOrbInternalId(), attributeName, referenceValue)
		
		then:
		thrown Exception
	}

	private void assertOrbPropertySize(Map<Long, OrbSteamerTrunk> map, long propertySize) {
		Set<Long> idSet = map.keySet()
		for (long orbInternalId : idSet) {
			def orbSteamerTrunk = map.get(orbInternalId)
			assertTrue orbSteamerTrunk.orb.userDefinedProperties.size() == propertySize
			
			logger.debug("id {}; cacheEntry attribute size: {}", orbInternalId, orbSteamerTrunk.cacheEntry.attributes.size())
			
			Assert.assertEquals propertySize, orbSteamerTrunk.cacheEntry.attributes.size()
		}
	}
	
	private long createOrbType() {
		int orbTypeInternalId = internalIdGenerator.getNewId();

		AddOrbTypeDto addOrbTypeDto = new AddOrbTypeDto("test", orbTypeInternalId);

		BigDecimal tranDate = tranDateGenerator.getTranDate()
		
		logger.debug("Tran Date: {}", tranDate.toString());

		UndoActionBundle undoActionBundle = new UndoActionBundle();

		return orbTypeManager.createOrbType(addOrbTypeDto, tranDate, undoActionBundle);
	}
}
