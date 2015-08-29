package com.fletch22.orb.cache.local;

import static org.junit.Assert.assertTrue
import junit.framework.Assert

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
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.client.service.RollbackTransactionService
import com.fletch22.orb.command.orbType.dto.AddOrbDto
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
import com.fletch22.orb.rollback.UndoActionBundle


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
	OrbReference orbReference;
	
	def setup() {
		orbTypeCollection = cache.orbTypeCollection;
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
		orbCollection = cache.orbCollection;
	}
	
	def cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
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
		
		BigDecimal tranDate = tranDateGenerator.getTranDate()
		Orb orbToTarget = orbManager.createOrb(orbTypeInternalId, tranDate)
		
		def numberOrbs = 1
		
		numberOrbs.times {
			
			tranDate = tranDateGenerator.getTranDate()
			logger.info("Tran Date: {}", tranDate.toString());
			
			Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)
			
			def referenceValue = orbReference.composeReference(orbToTarget.getOrbInternalId(), "foo");
			
			orbManager.setAttribute(orb.getOrbInternalId(), attributeName, referenceValue);
		}
		
		Assert.assertEquals numberOrbs, orbReference.@referenceCollection.countArrows()
		 
		Map<Long, OrbSteamerTrunk> map = orbCollection.getQuickLookup()
		assertOrbPropertySize(map, 1)
		
		def tranId = beginTransactionService.beginTransaction()
		
		orbTypeManager.deleteAttribute(orbTypeInternalId, attributeName)
		
		map.size() == numberOrbs
		assertOrbPropertySize(map, 0)
		
		Assert.assertEquals 0, orbReference.@referenceCollection.countArrows()
		
		when:
		rollbackTransactionService.rollbackToSpecificTransaction(tranId)
		
		then:
		assertOrbPropertySize(map, 1)
		logger.info("Arrows: {}", orbReference.@referenceCollection.countArrows())
		orbReference.@referenceCollection.countArrows() == numberOrbs
	}
	
	def "disallowReferenceOfReference"() {
		
		given:
		long orbTypeInternalId = createOrbType()
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		def attributeName = 'foo'
		orbTypeManager.addAttribute(orbType.id, attributeName)
		
		BigDecimal tranDate = tranDateGenerator.getTranDate()
		logger.info("Tran Date: {}", tranDate.toString());
		
		Orb orb1 = orbManager.createOrb(orbTypeInternalId, tranDate)
		
		tranDate = tranDateGenerator.getTranDate()
		Orb orb2 = orbManager.createOrb(orbTypeInternalId, tranDate)
		def referenceValue = orbReference.composeReference(orb1.getOrbInternalId(), "foo")
		orbManager.setAttribute(orb2.getOrbInternalId(), attributeName, referenceValue)

		tranDate = tranDateGenerator.getTranDate()
		Orb orb3 = orbManager.createOrb(orbTypeInternalId, tranDate)
		referenceValue = orbReference.composeReference(orb2.getOrbInternalId(), "foo")
		
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
			
			logger.info("id {}; cacheEntry attribute size: {}", orbInternalId, orbSteamerTrunk.cacheEntry.attributes.size())
			
			Assert.assertEquals propertySize, orbSteamerTrunk.cacheEntry.attributes.size()
		}
	}
	
	private long createOrbType() {
		int orbTypeInternalId = internalIdGenerator.getNewId();

		AddOrbTypeDto addOrbTypeDto = new AddOrbTypeDto("test", orbTypeInternalId);

		BigDecimal tranDate = tranDateGenerator.getTranDate()
		
		logger.info("Tran Date: {}", tranDate.toString());

		UndoActionBundle undoActionBundle = new UndoActionBundle();

		return orbTypeManager.createOrbType(addOrbTypeDto, tranDate, undoActionBundle);
	}
}
