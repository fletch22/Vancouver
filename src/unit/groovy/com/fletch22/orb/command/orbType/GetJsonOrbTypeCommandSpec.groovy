package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class GetJsonOrbTypeCommandSpec extends Specification {

	@Autowired
	GetOrbTypeCommand getJsonOrbTypeCommand
	
	@Test
	def 'test Get Json OrbTypeCommand parse and unparse'() {
		
		given:
		int orbInternalTypeIdExpected
		def json = this.getJsonOrbTypeCommand.toJson(orbInternalTypeIdExpected).toString()
		
		when:
		def action = this.getGetJsonOrbTypeCommand().fromJson(json)
		
		then:
		action.orbTypeInternalId == orbInternalTypeIdExpected
	}
}
