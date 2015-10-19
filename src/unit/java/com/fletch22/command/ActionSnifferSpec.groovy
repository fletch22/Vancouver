package com.fletch22.command;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.command.ActionSniffer
import com.fletch22.orb.command.orbType.AddOrbTypeCommand

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class ActionSnifferSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(ActionSnifferSpec);
	
	@Autowired
	ActionSniffer actionSniffer
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand

	def 'test root verb sniffer'() {
		
		given:
		def json = addOrbTypeCommand.toJson('foo');
		
		when:
		def verb = this.actionSniffer.getVerb(json);
		
		then:
		verb
		verb == CommandExpressor.ADD_ORB_TYPE
	}
	
	def 'test root verb sniffer multi'() {
		
		given:
		def json = addOrbTypeCommand.toJson('foo');
		def verb
		
		when:
		1000.times {
			verb = this.actionSniffer.getVerb(json);
		}
		
		then:
		verb
		verb == CommandExpressor.ADD_ORB_TYPE
	}
}
