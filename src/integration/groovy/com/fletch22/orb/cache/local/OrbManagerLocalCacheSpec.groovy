package com.fletch22.orb.cache.local;

import static org.junit.Assert.*

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.StopWatch
import org.junit.Test
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
import com.fletch22.util.RandomUtil
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

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
	OrbReference orbReference;

	def setup()  {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	def tearDown() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	@Test
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

	@Test
	def 'testDeleteInstance'() {

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
		orbManager.deleteOrb(orb.orbInternalId)

		then:
		!orbManager.doesOrbExist(orb.orbInternalId);
	}

	def testSetReferenceAttribute() {

		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foop", new LinkedHashSet<String>())

		String attributeName = "foo"
		orbTypeManager.addAttribute(orbTypeInternalId, attributeName)

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		String tranDateString = orbType.tranDate.toString()
		BigDecimal tranDate = new BigDecimal(tranDateString)

		Orb orb = orbManager.createOrb(orbTypeInternalId, tranDate)

		StopWatch stopWatch = new StopWatch()

		def numberOfReferences = 100
		def set1 = getSet(numberOfReferences)
		def set2 = getSet(numberOfReferences)

		def numberSetActions = 100

		stopWatch.start()
		def index = 0
		numberSetActions.times {
			index++
			def setToUse = index % 2 > 0 ? set1: set2 
			
			orbManager.setAttribute(orb.getOrbInternalId(), attributeName, setToUse)
		}
		stopWatch.stop()

		def elapsedMillisPerSetAction = new BigDecimal(stopWatch.getNanoTime()).divide(1000000).divide(numberSetActions)

		when:
		logger.info("Each set action averaged {} millis", elapsedMillisPerSetAction)
		def fetchedValue = orbManager.getAttribute(orb.orbInternalId, attributeName)

		then:
		fetchedValue == set1 ? true: fetchedValue == set2 ? true: false 
	}

	def getSet(int numberOfReferences) {
		Set<String> set = new HashSet<String>()
		numberOfReferences.times {
			def orbInternalId = randomUtil.getRandom(1, 1000)
			def attributeName = randomUtil.getRandomString(10)
			def composedKey = orbReference.composeReference(orbInternalId, attributeName)
			set.add(composedKey)
		}

		return StringUtils.join(set, ',');
	}
}
