package com.fletch22.redis;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext.xml")
class OrbInstanceServiceSpec extends Specification {
	
	@Autowired
	OrbInstanceService orbInstanceService

	@Test
	def 'test instance exists'() {
		
		given:
		when:
		orbInstanceService
		
		then:
		orbInstanceService
	}

}
