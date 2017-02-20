package com.fletch22.orb.service;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.client.service.OrbTypeService
import com.fletch22.util.RandomUtil

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class OrbTypeServiceIntegrationSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(OrbTypeServiceIntegrationSpec)
	
	@Autowired
	OrbTypeService orbTypeService
	
	@Autowired
	RandomUtil randomUtil = new RandomUtil()
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	def setup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}
	
	def cleanup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	def 'test create orb type'() {
		
		given:
		String label = randomUtil.getRandomString();
		
		logger.debug("Label: {}", label);
		
		when:
		long orbInternalId = orbTypeService.addOrbType(label);
		
		then:
		orbInternalId > 0;
	}
	
	def 'test volume create'() {
		
		given:
		String label = randomUtil.getRandomString();
		int max = 10
		
		when:
		logger.debug("Start create type.");
		for (int i = 0; i < max; i++) {
			String labelToUse = label + String.valueOf(i)
			long orbInternalId = orbTypeService.addOrbType(labelToUse)
		}
		logger.debug("End create type.");
		
		then:
		1 == 1
	}

}
