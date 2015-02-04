package com.fletch22.orb.service;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

import com.fletch22.util.RandomUtil


@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class OrbTypeServiceSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(OrbTypeServiceSpec)
	
	@Autowired
	OrbTypeService orbTypeService
	
	@Autowired
	RandomUtil randomUtil = new RandomUtil();
	
	def cleanup() {
		// remove all orb types created.
		
	}

	@Test
	def 'test create orb type'() {
		
		given:
		String label = randomUtil.getRandomString();
		
		logger.info("Label: {}", label);
		
		when:
		long orbInternalId = orbTypeService.addOrbType(label);
		
		then:
		orbInternalId > 0;
	}
	
	@Test
	def 'test volume create'() {
		
		given:
		String label = randomUtil.getRandomString();
		int max = 10000
		
		when:
		logger.info("Start create type.");
		for (int i = 0; i < max; i++) {
			String labelToUse = label + String.valueOf(i)
			long orbInternalId = orbTypeService.addOrbType(labelToUse)
		}
		logger.info("End create type.");
		
		then:
		1 == 1
	}

}