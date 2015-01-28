/**
 * 
 */
package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext.xml')
class GetListOfOrbTypesTranslatorSpec extends Specification {

	@Autowired
	GetListOfOrbTypesCommand getListOfOrbTypesTranslator
	
	@Test
	def 'test translation'() {
		
		given:
		def expectedSearchString = 'foo'
		def action = this.getListOfOrbTypesTranslator.getActionCommand(expectedSearchString);
		
		when:
		def actionData = this.getListOfOrbTypesTranslator.getActionData(action.toString());
		
		then:
		actionData.searchString == expectedSearchString;
	}
}
