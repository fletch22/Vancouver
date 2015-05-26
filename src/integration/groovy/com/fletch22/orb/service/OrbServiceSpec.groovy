package com.fletch22.orb.service

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb

@org.junit.experimental.categories.Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class OrbServiceSpec {

	@Autowired
	OrbService orbService

	@Autowired
	OrbTypeService orbTypeService

	@Test
	def void 'test create orb'() {

		given:
		def orbTypeInternalId = orbTypeService.addOrbType("test")

		when:
		Orb orb = orbService.addOrb(orbTypeInternalId)

		then:
		orb
	}
}
