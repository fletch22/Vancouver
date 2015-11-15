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
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.command.transaction.RollbackTransactionService
import com.fletch22.orb.query.CriteriaFactory.Criteria

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class QueryManagerSpec extends Specification {

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	QueryManager queryManager
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	OrbManager orbManager
	
	@Autowired
	CriteriaFactory criteriaFactory
	
	@Autowired
	Cache cache
	
	@Autowired
	BeginTransactionService beginTransactionService
	
	@Autowired
	RollbackTransactionService rollbackTransactionService
	
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
		queryManager.create(criteria)
		
		when:
		Criteria criteriaFound = queryManager.findQuery(orbTypeInternalId, queryLabel);
		
		then:
		criteriaFound
	}

	def 'test handle type delete event'() {
		
		given:
		Criteria criteria = createSampleQuery()
		
		Orb queryOrb = orbManager.getOrb(criteria.getCriteriaId())
		assertNotNull queryOrb
		assertNotNull cache.orbCollection
		
		when:
		queryManager.handleTypeDeleteEvent(criteria.getOrbTypeInternalId(), true)
		
		then:
		!orbManager.doesOrbExist(criteria.getCriteriaId())
	}

	private Criteria createSampleQuery() {
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		String queryLabel = 'fuzzyThings'
		Criteria criteria = criteriaFactory.createInstance(orbType, queryLabel)
		long queryId = queryManager.create(criteria)
		return criteria
	}
	
	def 'test query add rollback'() {
		
		given:
		def tranId = beginTransactionService.beginTransaction()
		
		Criteria criteria = createSampleQuery()
		
		when:
		this.rollbackTransactionService.rollbackToSpecificTransaction(tranId)
		
		then:
		!queryManager.doesQueryExist(criteria.getCriteriaId())
		!orbManager.doesOrbExist(criteria.getCriteriaId());
	}
	
	def 'test query remove rollback'() {
		
		given:
		Criteria criteria = createSampleQuery()
		def tranId = beginTransactionService.beginTransaction()
		
		def doesExist = queryManager.doesQueryExist(criteria.getCriteriaId())
		assertTrue doesExist
		
		queryManager.removeFromCollection(criteria.getCriteriaId())
		
		when:
		this.rollbackTransactionService.rollbackToSpecificTransaction(tranId)
		
		then:
		queryManager.doesQueryExist(criteria.getCriteriaId())
		orbManager.doesOrbExist(criteria.getCriteriaId());
	}
}
