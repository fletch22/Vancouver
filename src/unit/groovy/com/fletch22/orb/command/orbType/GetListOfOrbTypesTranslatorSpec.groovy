/**
 * 
 */
package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class GetListOfOrbTypesTranslatorSpec extends Specification {

	@Autowired
	GetListOfOrbTypesCommand getListOfOrbTypesTranslator
	
	def 'test translation'() {
		
		given:
		def expectedSearchString = 'foo'
		def action = this.getListOfOrbTypesTranslator.toJson(expectedSearchString);
		
		when:
		def actionData = this.getListOfOrbTypesTranslator.fromJson(action.toString());
		
		then:
		actionData.searchString == expectedSearchString;
	}
}
