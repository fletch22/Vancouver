package com.fletch22.orb.query;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
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
import com.fletch22.orb.query.constraint.Constraint
import com.fletch22.orb.query.constraint.ConstraintDetailsAggregate

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class QueryManagerSpec extends Specification {
	
	@Shared
	Logger logger = LoggerFactory.getLogger(QueryManagerSpec.class)

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
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
	
	@Autowired
	QueryMother queryMother
	
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
		queryManager.addToCollection(criteria)
		
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
		
		def doesExist = ((QueryManagerImpl) queryManager).getCriteriaCollection().doesCriteriaExistWithOrbTypeInternalId(criteria.getOrbTypeInternalId())
		assertTrue doesExist
		
		when:
		queryManager.handleTypeDeleteEvent(criteria.getOrbTypeInternalId(), true)
		
		then:
		!orbManager.doesOrbExist(criteria.getCriteriaId())
	}

	private Criteria createSampleQuery() {
		long orbTypeInternalId = orbTypeManager.createOrbType("foo", new LinkedHashSet<String>())
		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		orbTypeManager.addAttribute(orbTypeInternalId, "orangeFuzz")
		
		String queryLabel = 'fuzzyThings'
		Criteria criteria = criteriaFactory.createInstance(orbType, queryLabel)
		long queryId = queryManager.addToCollection(criteria)
		
		return criteria
	}
	
	def 'test query add rollback'() {
		
		given:
		def tranId = beginTransactionService.beginTransaction()
		
		Criteria criteria = queryMother.getAggregateQuery()
		Criteria criteriaAgg = extractAggregateCriteria(criteria)
		
		when:
		this.rollbackTransactionService.rollbackToSpecificTransaction(tranId)
		
		then:
		!queryManager.doesCriteriaExist(criteria.getCriteriaId())
		!orbManager.doesOrbExist(criteria.getCriteriaId());
		
		!queryManager.doesCriteriaExist(criteriaAgg.getCriteriaId())
		!orbManager.doesOrbExist(criteriaAgg.getCriteriaId());
	}
	
	def 'test query remove rollback'() {
		
		given:
		Criteria criteria = createSampleQuery()
		def tranId = beginTransactionService.beginTransaction()
		
		def doesExist = queryManager.doesCriteriaExist(criteria.getCriteriaId())
		assertTrue doesExist
		
		queryManager.delete(criteria.getCriteriaId(), true)
		
		when:
		this.rollbackTransactionService.rollbackToSpecificTransaction(tranId)
		
		then:
		queryManager.doesCriteriaExist(criteria.getCriteriaId())
		orbManager.doesOrbExist(criteria.getCriteriaId());
	}
	
	def 'test query add to collection'() {
		
		given:
		when:
		Criteria criteria = queryMother.getAggregateQuery()
		
		OrbType orbType = orbTypeManager.getOrbType(criteria.getOrbTypeInternalId())
		
		Criteria criteriaAgg = extractAggregateCriteria(criteria)
		
		then:
		queryManager.doesCriteriaExist(criteria.getCriteriaId())
		orbManager.doesOrbExist(criteria.getCriteriaId());
		
		queryManager.doesCriteriaExist(criteriaAgg.getCriteriaId())
		orbManager.doesOrbExist(criteriaAgg.getCriteriaId());

		criteria
		criteriaAgg.getParentId() != Criteria.UNSET_ID
	}
	
	def 'test query delete delete dependencies'() {
		
		given:
		def tranId = beginTransactionService.beginTransaction()
		
		Criteria criteria = queryMother.getAggregateQuery()
		Criteria criteriaAgg = extractAggregateCriteria(criteria)
		
		when:
		queryManager.delete(criteria.criteriaId, true)
		
		then:
		!queryManager.doesCriteriaExist(criteria.getCriteriaId())
		!orbManager.doesOrbExist(criteria.getCriteriaId());
		
		!queryManager.doesCriteriaExist(criteriaAgg.getCriteriaId())
		!orbManager.doesOrbExist(criteriaAgg.getCriteriaId());
	}
	
	def 'test query delete fail when dependencies'() {
		
		given:
		def tranId = beginTransactionService.beginTransaction()
		
		Criteria criteria = queryMother.getAggregateQuery()
		Criteria criteriaAgg = extractAggregateCriteria(criteria)
		
		when:
		queryManager.delete(criteria.criteriaId, false)
		
		then:
		def exception = thrown(Exception)
		exception
		exception.getMessage().contains("Orb has at least one dependency.")
	}
	
	Criteria extractAggregateCriteria(Criteria criteria) {
		Constraint constraintAgg = criteria.logicalConstraint.constraintList.get(0)
		
		assertNotNull(constraintAgg)
		assertTrue(constraintAgg instanceof ConstraintDetailsAggregate)
		
		return ((ConstraintDetailsAggregate) constraintAgg).criteriaForAggregation
	}
}
