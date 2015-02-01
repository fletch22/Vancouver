package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext.xml')
class BeginTransactionCommandSpec extends Specification {
	
	@Autowired
	BeginTransactionCommand beginTransactionCommand 

	@Test
	def 'test begin tranaction parse and unparse'() {
		
		given:
		def json = this.beginTransactionCommand.toJson().toString()
		
		when:
		this.beginTransactionCommand.fromJson(json)
		
		then:
		notThrown(Exception) 
	}
}
