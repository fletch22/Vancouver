package com.fletch22.orb.query;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.query.CriteriaFactory.Criteria

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class QueryManagerImplSpec extends Specification {

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	QueryManagerImpl queryManagerImpl
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	OrbManager orbManager
	
	@Autowired
	CriteriaFactory criteriaFactory
	
	@Autowired
	Cache cache
	
	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	def after() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	def testFindQuery() {
		
		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		String queryLabel = 'fuzzyThings'
		Criteria criteria = new Criteria(orbType, queryLabel)
		queryManagerImpl.create(criteria)
		
		when:
		Criteria criteriaFound = queryManagerImpl.findQuery(orbTypeInternalId, queryLabel);
		
		then:
		criteriaFound
	}

	def 'test handle type delete event'() {
		
		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
				
		String queryLabel = 'fuzzyThings'
		Criteria criteria = new Criteria(orbType, queryLabel)
		long queryId = queryManagerImpl.create(criteria)
		
		Orb queryOrb = orbManager.getOrb(criteria.getCriteriaId())
		assertNotNull queryOrb
		assertNotNull cache.orbCollection
		
		when:
		queryManagerImpl.handleTypeDeleteEvent(orbTypeInternalId, true)
		
		then:
		!orbManager.doesOrbExist(criteria.getCriteriaId())
	}
}
