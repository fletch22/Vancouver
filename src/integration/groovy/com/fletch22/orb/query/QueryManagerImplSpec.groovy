package com.fletch22.orb.query;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.query.CriteriaFactory.Criteria

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class QueryManagerImplSpec extends Specification {

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	QueryManagerImpl criteriaManagerImpl;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	def after() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	public void testFindQuery() {
		
		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		String queryLabel = 'fuzzyThings'
		Criteria criteria = new Criteria(orbType, queryLabel)
		criteriaManagerImpl.create(criteria)
		
		when:
		Criteria criteriaFound = criteriaManagerImpl.findQuery(orbTypeInternalId, queryLabel);
		
		then:
		criteriaFound
	}

}
