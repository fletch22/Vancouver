package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class BeginTransactionCommandSpec extends Specification {
	
	@Autowired
	BeginTransactionCommand beginTransactionCommand 

	def 'test begin tranaction parse and unparse'() {
		
		given:
		def json = this.beginTransactionCommand.toJson().toString()
		
		when:
		this.beginTransactionCommand.fromJson(json)
		
		then:
		notThrown(Exception) 
	}
}
