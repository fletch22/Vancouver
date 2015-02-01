package com.fletch22.command;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.fletch22.orb.command.orbType.AddOrbTypeCommand;

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext.xml')
class CommandFactorySpec extends Specification {
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand
	
	@Autowired
	CommandFactory commandFactory

	@Test
	def 'test factory'() {
		
		given:
		def action = this.addOrbTypeCommand.toJson('foo').toString()
		
		when:
		def jsonCommand = this.commandFactory.getJsonCommand(action)
		
		then:
		notThrown(Exception)
		jsonCommand
	}

}
