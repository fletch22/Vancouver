package com.fletch22.orb.command.orbType;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.Orb
import com.fletch22.orb.command.orbType.dto.AddWholeOrbTypeDto

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class AddWholeOrbTypeCommandSpec extends Specification {
	
	@Autowired
	AddWholeOrbTypeCommand addWholeOrbTypeCommand

	def 'test serialize and deserialize AddWholeOrbTypeCommand'() {
		
		given:
		when:
		Orb orbOriginal = new Orb();
		orbOriginal.orbInternalId = 123.toLong()
		orbOriginal.tranDate = new BigDecimal("2132134423142314123")
		orbOriginal.userDefinedProperties = new HashMap<String, String>()
		
		orbOriginal.userDefinedProperties.put("foo1", "foo1Value")
		orbOriginal.userDefinedProperties.put("foo2", "foo2Value")
		
		StringBuilder json = this.addWholeOrbTypeCommand.toJson(orbOriginal)
		AddWholeOrbTypeDto addWholeOrbTypeDto = this.addWholeOrbTypeCommand.fromJson(json.toString())
		
		then:
		orbOriginal.orbInternalId == addWholeOrbTypeDto.orb.orbInternalId
		orbOriginal.tranDate.toString() == addWholeOrbTypeDto.orb.tranDate.toString()
		
		Map<String, String> propertiesOriginal = orbOriginal.getUserDefinedProperties()
		Map<String, String> propertiesActual = addWholeOrbTypeDto.orb.getUserDefinedProperties()
		propertiesActual.size() == propertiesOriginal.size()
		for (String key: propertiesOriginal.keySet()) {
			String valueOriginal = properties.get(key)
			String valueActual = propertiesActual.get(key)
			valueOriginal == valueActual
		}
	}
}
