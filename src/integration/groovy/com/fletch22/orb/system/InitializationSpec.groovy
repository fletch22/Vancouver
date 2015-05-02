package com.fletch22.orb.system

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.dao.LogActionDao
import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class InitializationSpec extends Specification {
	
	@Autowired
	Initialization initialization;
	
	@Autowired
	LogActionDao logActionDao;

//	@Test
//	def 'test'() {
//		
//		given:
//		this.logActionDao.recordTransactionStart(this.logActionDao.)
//		
//		when:
//		this.initialization.initializeSystem();
//		
//		then:
//		
//	}
	
}
