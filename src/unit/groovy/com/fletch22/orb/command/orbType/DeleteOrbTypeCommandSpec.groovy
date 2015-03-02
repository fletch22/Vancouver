package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class DeleteOrbTypeCommandSpec extends Specification {

	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand
	
	@Test
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
