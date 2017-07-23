package com.fletch22.app.designer;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.ddl.DropDownListbox

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class ViewAttributesCollectorSpec extends Specification {
	
	@Autowired
	ViewAttributesCollector viewAttributesCollector

	def 'test collector'() {
		given:
		when:
			Map<String, Set<String>> map = viewAttributesCollector.collect()
		then:
			assertNotNull(viewAttributesCollector)
			assertTrue(map.size() > 0)
			assertTrue(map.keySet().contains(DropDownListbox.TYPE_LABEL));
	}

}
