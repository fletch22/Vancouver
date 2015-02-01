package com.fletch22.command;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.command.orbType.AddOrbTypeCommand

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext.xml')
class ActionSnifferSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(ActionSnifferSpec);
	
	@Autowired
	ActionSniffer actionSniffer
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand

	@Test
	def 'test root verb sniffer'() {
		
		given:
		def json = addOrbTypeCommand.toJson('foo').toString();
		
		when:
		def verb = this.actionSniffer.getVerb(json);
		
		then:
		verb
		verb == CommandExpressor.ADD_ORB_TYPE
	}

}
