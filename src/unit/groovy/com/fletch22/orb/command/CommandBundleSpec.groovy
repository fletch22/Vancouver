package com.fletch22.orb.command;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.fletch22.util.json.GsonFactory

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class CommandBundleSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(CommandBundleSpec)
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	GsonFactory gsonFactory
	
	def 'test serialization and deserialization'() {
		
		given:
		def label = 'asdfasdf'
		CommandBundle commandBundle = new CommandBundle()
		
		commandBundle.addCommand(addOrbTypeCommand.toJson(label))
		
		def jsonExpected = commandBundle.toJson()
		
		assertNotNull(jsonExpected)
		
		def commandBundleActual = CommandBundle.fromJson(gsonFactory.getInstance(), jsonExpected)
		
		when:
		def jsonActual = commandBundleActual.toJson()
		
		then:
		jsonActual.toString() == jsonExpected.toString()
	}

}
