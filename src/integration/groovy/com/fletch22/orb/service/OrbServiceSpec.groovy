package com.fletch22.orb.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.client.service.OrbService
import com.fletch22.orb.client.service.OrbTypeService

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class OrbServiceSpec extends Specification {

	@Autowired
	OrbService orbService

	@Autowired
	OrbTypeService orbTypeService

	def 'test create orb'() {

		given:
		def orbTypeInternalId = orbTypeService.addOrbType("test")

		when:
		Orb orb = orbService.addOrb(orbTypeInternalId)

		then:
		orb
	}
}
