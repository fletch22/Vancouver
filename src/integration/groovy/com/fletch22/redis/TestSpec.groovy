package com.fletch22.redis;

import static org.junit.Assert.*;

import org.junit.Test;

import spock.lang.Specification;

class TestSpec extends Specification {

	@Test
	def 'test'() {
		given:
		def test = ''
		
		when:
		test = '2t'
		
		then:
		assert test
	}

}
