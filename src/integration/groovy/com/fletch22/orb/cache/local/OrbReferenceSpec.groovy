package com.fletch22.orb.cache.local;

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
import com.fletch22.orb.cache.reference.OrbReference
import com.fletch22.orb.cache.reference.ReferenceUtil
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.command.transaction.RollbackTransactionService
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder
import com.fletch22.orb.test.data.TestDataSimple

@org.junit.experimental.categories.Category(IntegrationTests)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class OrbReferenceSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(OrbReferenceSpec)
	
	@Shared
	OrbReference orbReference
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	OrbManager orbManager
	
	@Autowired
	ReferenceUtil referenceUtil
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	Cache cache
	
	@Autowired
	TestDataSimple testDataSimple
	
	@Autowired
	BeginTransactionService beginTransactionService
	
	@Autowired
	RollbackTransactionService rollbackTransactionService
	
	@Autowired
	EventLogCommandProcessPackageHolder eventLogCommandProcessPackageHolder
	
	def setup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems()
		this.orbReference = cache.orbCollection.orbReference
	}
	
	def cleanup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems()
	}

	def 'test Decomposition'() {
		
		given:
		long orbInternalId = 1234
		String attributeName = "foo-manchu"
		
		String composedKey = referenceUtil.composeReference(orbInternalId, attributeName)
		
		when:
		def decomposedKey = referenceUtil.decomposeKey(composedKey);
		
		then:
		decomposedKey.orbInternalId == orbInternalId
		decomposedKey.attributeName == attributeName
	}
	
	def 'test addReference First Reference'() {
		
		given:
		
		
		Orb orb = new Orb()
		orb.orbInternalId = 123
		
		when:
		StringBuffer newValue = orbReference.addReference(234, 'color', new StringBuffer(''), 123)
		
		then:
		orbReference
		newValue
		
		int count = orbReference.countArrowsPointToTarget(orb)
		count == 1
		
	}
	
	def 'test addReference Same'() {
		
		given:
		Orb orb = new Orb()
		orb.orbInternalId = 123
		
		orbReference.addReference(234, 'color', new StringBuffer(''), 123)
		
		when:
		StringBuffer newValue = orbReference.addReference(234, 'color', new StringBuffer(''), 123)
		
		then:
		orbReference
		newValue
		
		int count = orbReference.countArrowsPointToTarget(orb)
		count == 1
	}
	
	def 'test add orb reference rollback'() {
		
		given:
		long orbTypeInternalId = testDataSimple.loadTestData()
		
		def tranId = beginTransactionService.beginTransaction()
		
		Orb orbTarget = orbManager.createOrb(orbTypeInternalId)

		Orb orbArrow = orbManager.createOrb(orbTypeInternalId)
		
		int count = orbReference.countArrowsPointToTarget(orbTarget)
		assertEquals(count, 0)
		
		orbManager.addReference(orbArrow.getOrbInternalId(), TestDataSimple.ATTRIBUTE_COLOR, orbTarget.getOrbInternalId())
		
		count = orbReference.countArrowsPointToTarget(orbTarget)
		assertEquals(count, 1)
		
		def doesExist = orbManager.doesOrbExist(orbTarget.getOrbInternalId())
		assertTrue(doesExist)
		
		when:
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId)
				
		then:
		int countx = orbReference.countArrowsPointToTarget(orbTarget)
		countx == 0
	}
	
	def 'test add attr reference rollback'() {
		
		given:
		
		def tranId = beginTransactionService.beginTransaction()
		logger.debug("Tran ID: {}", tranId)
		
		long orbTypeInternalId = testDataSimple.loadTestData()

		Orb orbTarget = orbManager.createOrb(orbTypeInternalId)

		Orb orbArrow = orbManager.createOrb(orbTypeInternalId)
		
		int count = orbReference.countArrowsPointToTarget(orbTarget)
		assertEquals(count, 0)
		
		orbManager.addReference(orbArrow.getOrbInternalId(), TestDataSimple.ATTRIBUTE_COLOR, orbTarget.getOrbInternalId(), TestDataSimple.ATTRIBUTE_COLOR)
		
		count = orbReference.countArrowsPointToTarget(orbTarget)
		assertEquals(count, 1)
		
		when:
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId)
		
		then:
		int countx = orbReference.countArrowsPointToTarget(orbTarget)
		countx == 0
		
	}
	
	def 'test 2 serialized trans'() {
		
		given:
		def tranId = beginTransactionService.beginTransaction()
		logger.debug("Tran ID: {}", tranId)
		
		long orbTypeInternalId1 = orbTypeManager.createOrbType("fooManChu43435", new LinkedHashSet<String>());
		
		def doesExist = orbTypeManager.doesOrbTypeExist(orbTypeInternalId1)
		assertTrue(doesExist)
		
		logger.debug("Rollback back first tran.")
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId)
		
		doesExist = orbTypeManager.doesOrbTypeExist(orbTypeInternalId1)
		assertFalse(doesExist)
		
		tranId = beginTransactionService.beginTransaction()
		logger.debug("Tran ID: {}", tranId)
		
		long orbTypeInternalId2 = orbTypeManager.createOrbType("muckery23424", new LinkedHashSet<String>());
		
		when:
		logger.debug("Rolling back 2nd tran.")
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId)
		
		then:
		boolean doesExistNow = orbTypeManager.doesOrbTypeExist(orbTypeInternalId2)
		assertFalse(doesExistNow)
	}
}


