package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class GetOrbTypeFromLabelCommandSpec extends Specification {
	
	@Autowired
	GetOrbTypeFromLabelCommand getOrbTypeFromLabelCommand

	def 'test parse and unparse'() {
		
		given:
		def labelExpected = 'foo'
		def json = this.getOrbTypeFromLabelCommand.toJson(labelExpected).toString()
		
		when:
		def action = this.getOrbTypeFromLabelCommand.fromJson(json)
		
		then:
		action.label == labelExpected
	}
}
