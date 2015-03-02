package com.fletch22.orb.command;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.command.orbType.AddOrbTypeCommand

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CommandBundleSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandBundleSpec)
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Test
	def 'test serialization and deserialization'() {
		
		given:
		def label = 'asdfasdf'
		CommandBundle commandBundle = new CommandBundle()
		
		commandBundle.addCommand(addOrbTypeCommand.toJson(label))
		
		def jsonExpected = commandBundle.toJson()
		
		assertNotNull(jsonExpected)
		
		logger.info("Is JE null? {}", null == jsonExpected)
		logger.info('JE: {}', jsonExpected);
		
		def commandBundleActual = CommandBundle.fromJson(jsonExpected)
		
		when:
		def jsonActual = commandBundleActual.toJson()
		
		logger.info("JA: {}", jsonActual);
				
		then:
		jsonActual.toString() == jsonExpected.toString()
	}

}
