package com.fletch22.orb.cache.local;

import static org.junit.Assert.assertTrue

import org.junit.Test
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
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType
import com.fletch22.orb.command.orbType.dto.AddOrbDto
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
import com.fletch22.orb.rollback.UndoActionBundle


@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbCollectionSpec extends Specification {

	@Shared Logger logger = LoggerFactory.getLogger(OrbCollectionSpec.class);

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	OrbManager orbManager;

	@Autowired
	Cache cache;
	
	OrbCollection orbCollection;

	OrbTypeCollection orbTypeCollection;
	
	def setup() {
		orbTypeCollection = cache.orbTypeCollection;
		orbCollection = cache.orbCollection;
	}

	@Test
	def 'testSuccess'() {

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
	
	private long createOrbType() {
		int orbTypeInternalId = 1;

		AddOrbTypeDto addOrbTypeDto = new AddOrbTypeDto("test", orbTypeInternalId);

		BigDecimal tranDate = new BigDecimal("1");

		UndoActionBundle undoActionBundle = new UndoActionBundle();

		return orbTypeManager.createOrbType(addOrbTypeDto, tranDate, undoActionBundle);
	}
}
