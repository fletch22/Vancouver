package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import com.fletch22.orb.command.orbType.dto.UpdateOrbTypeLabelDto;

import spock.lang.Specification

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class UpdateOrbTypeLabelTranslatorSpec extends Specification {
	
	@Autowired
	UpdateOrbTypeLabelCommand updateOrbTypeLabelCommand; 

	def 'test translation'() {
		
		given:
		String expectedLabel = 'foo'
		int expectedInternalId = 123
		
		String action = this.updateOrbTypeLabelCommand.toJson(expectedInternalId, expectedLabel)
		
		when:
		UpdateOrbTypeLabelDto dto = this.updateOrbTypeLabelCommand.fromJson(action);
		
		then:
		dto.label == expectedLabel
		dto.orbTypeInternalId == expectedInternalId 
	}
}
