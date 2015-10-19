package com.fletch22.orb.command;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.command.orbType.AddOrbTypeCommand

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CommandBundleSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandBundleSpec)
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	def 'test serialization and deserialization'() {
		
		given:
		def label = 'asdfasdf'
		CommandBundle commandBundle = new CommandBundle()
		
		commandBundle.addCommand(addOrbTypeCommand.toJson(label))
		
		def jsonExpected = commandBundle.toJson()
		
		assertNotNull(jsonExpected)
		
		def commandBundleActual = CommandBundle.fromJson(jsonExpected)
		
		when:
		def jsonActual = commandBundleActual.toJson()
		
		then:
		jsonActual.toString() == jsonExpected.toString()
	}

}
