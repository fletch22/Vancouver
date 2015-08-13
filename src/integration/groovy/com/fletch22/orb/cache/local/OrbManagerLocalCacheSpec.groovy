package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Ignore
import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbManagerLocalCacheSpec extends Specification {
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	OrbManager orbManager
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	def setup()  {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}
	
	def tearDown() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
	def 'testAddIllegalAttributeValue'() {

		// Arrange
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
	
	
	@Test
	@Ignore
	def 'testDeleteInstance'() {

		// Arrange
		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foop", new LinkedHashSet<String>())

		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName)
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		String tranDateString = orbType.tranDate.toString()
		BigDecimal tranDate = new BigDecimal(tranDateString)

		Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)
		
		when:
		orbManager.deleteOrb(orb.orbInternalId)

		then:
		Orb orbOld = orbManager.getOrb(orb.orbInternalId);
	}

}
