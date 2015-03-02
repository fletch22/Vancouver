package com.fletch22.orb;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class OrbTransformerSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(OrbTransformerSpec)
	
	@Autowired
	OrbTransformer orbTransformer
	
	def setup() {
		
	}

	@Test
	def 'test orb serialize and deserialize'() {
		
		given:
		setup()
		
		Orb orb = new Orb();
		
		BigDecimal tranDateExpected = new BigDecimal("1234123312423414231");
		
		long orbInternalIdExpected = 2134
		
		orb.setOrbInteralId(orbInternalIdExpected);
		orb.setTranDate(tranDateExpected);
		
		def userDefinedKey1 = 'foo1'
		def userDefinedKey2 = 'foo2'
		def userDefinedValueExpected1 = 'fooValue1'
		def userDefinedValueExpected2 = 'fooValue2'
		
		Map<String, String> map = new HashMap<>()
		map.put(userDefinedKey1, userDefinedValueExpected1)
		map.put(userDefinedKey2, userDefinedValueExpected2)
		
		orb.setUserDefinedProperties(map)
		
		when:
		def json = orbTransformer.convertOrbToJson(orb)
		def orbActual = orbTransformer.convertFromJson(json.toString())
		
		then:
		json
		orbActual
		orbActual.getUserDefinedProperties().containsKey(userDefinedKey1)
		
		def userDefinedValue1 = orbActual.getUserDefinedProperties().get(userDefinedKey1)
		def userDefinedValue2 = orbActual.getUserDefinedProperties().get(userDefinedKey2)
		
		orbActual.getUserDefinedProperties().containsKey(userDefinedKey2)
		orbActual.orbInteralId == orbInternalIdExpected
		userDefinedValue1 == userDefinedValueExpected1
		userDefinedValue2 == userDefinedValueExpected2
		orbActual.getUserDefinedProperties().size() == 2
	}
}
