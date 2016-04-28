

package com.fletch22.app.designer.service;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.dao.BaseDao
import com.fletch22.orb.IntegrationTests

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class BaseComponentServiceSpec extends Specification {

	@Autowired
	BaseDao baseDao;
	
	def 'test baseDao'() {
		
		given:
		String test = 'test'
		
		when:
		test = 'test'
				
		then:
		baseDao != null
	}

}
