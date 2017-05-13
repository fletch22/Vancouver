package com.fletch22;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.web.controllers.UserDataController
import com.fletch22.web.controllers.UserDataController.PersistOrbInfo

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class UserDataControllerSpec extends Specification {
	
	@Autowired
	UserDataController userDataController
		
	Logger logger = LoggerFactory.getLogger(UserDataControllerSpec);
	
	@Test
	def 'test persist (save) orb parser'() {
		given:
		def json = '{"collectionId":1176,"row":{"id":"-","attributes":{"f1":"asdf","f2":"sfdg"}}}'

		when:
		PersistOrbInfo persistOrbInfo = userDataController.parseJsonPersistOrbInfo(json)
		
		then:
		userDataController != null
		persistOrbInfo.orbTypeInternalId == 1176
		persistOrbInfo.orbInternalId == Optional.empty()
		persistOrbInfo.attributes.get("f1") == "asdf"
		persistOrbInfo.attributes.get("f2") == "sfdg"
	}
}

