package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class DeleteOrbTypeCommandSpec extends Specification {

	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand
	
	def 'test serial and unserialize'() {
		
		given:
		long expectedOid = 123
		boolean expectedAllowCascadingDeletes = false
		def action = this.deleteOrbTypeCommand.toJson(expectedOid, expectedAllowCascadingDeletes)
		
		when:
		DeleteOrbTypeDto deleteOrbTypeDto = this.deleteOrbTypeCommand.fromJson(action.toString())
		
		then:
		deleteOrbTypeCommand
		deleteOrbTypeDto.orbTypeInternalId == expectedOid
		deleteOrbTypeDto.allowCascadingDeletes == expectedAllowCascadingDeletes
	}
}
