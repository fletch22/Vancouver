package com.fletch22.orb.query

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;

@ContextConfiguration(locations = "classpath:/springContext-test.xml")
@Component
public class QueryTestData {
	
	Logger logger = LoggerFactory.getLogger(QueryTestData)
	
	@Autowired
	OrbManager orbManager

	@Autowired
	OrbTypeManager orbTypeManager
	
	static final String ATTRIBUTE_COLOR = 'color'
	static final String ATTRIBUTE_SIZE = 'size'

	static final String COLOR_TO_FIND = 'green'

	public long loadTestData() {

		LinkedHashSet<String> customFields = new LinkedHashSet<String>()

		customFields.add(ATTRIBUTE_COLOR)
		customFields.add("size")
		customFields.add("speed")

		def orbTypeInternalId = orbTypeManager.createOrbType('foo', customFields)

		def color = 'red'
		
		def numInstances = 60
		setNumberInstancesToColor(60, orbTypeInternalId, "red")
		setNumberInstancesToColor(10, orbTypeInternalId, "orange")
		setNumberInstancesToColor(1, orbTypeInternalId, "puce")
		setNumberInstancesToColor(40, orbTypeInternalId, COLOR_TO_FIND)
		
		return orbTypeInternalId
	}

	private setNumberInstancesToColor(int numInstances, long orbTypeInternalId, color) {
		numInstances.times {
			Orb orb = orbManager.createOrb(orbTypeInternalId)
			orbManager.setAttribute(orb.orbInternalId, ATTRIBUTE_COLOR, color);
		}
	}
}
