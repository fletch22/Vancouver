package com.fletch22.orb.query;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.query.constraint.Constraint
import com.fletch22.orb.query.constraint.ConstraintDetailsAggregate
import com.fletch22.orb.query.constraint.ConstraintProcessor
import com.fletch22.orb.query.constraint.aggregate.Aggregate
import com.fletch22.orb.query.criteria.Criteria
import com.fletch22.orb.query.criteria.CriteriaAggregate
import com.fletch22.orb.query.criteria.CriteriaStandard

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class ConstraintProcessorSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(ConstraintProcessorSpec)
	
	@Autowired
	ConstraintProcessor constraintProcessor
	
	@Autowired
	QueryMother queryMother
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer
	
	@Autowired
	OrbTypeManager orbTypeManager
	
	@Autowired
	QueryManager queryManager
	
	@Autowired
	QueryTestData queryTestData
	
	@Autowired
	Cache cache
	
	@Shared
	OrbType orbType
	
	def setup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems()
		def orbTypeInternalId = queryTestData.loadTestData()
		
		logger.info("OITID: " + orbTypeInternalId)
		
		orbType = orbTypeManager.getOrbType(orbTypeInternalId)
	}

	def cleanup() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems()
	}

	def 'test getAttributeValuesByFrequency when only one key'() {
		
		given:
		String queryLabel = "fuzzyThings"
		Criteria criteria = new CriteriaStandard(orbType.id, queryLabel)
		
		String[] attributeNames = [QueryTestData.ATTRIBUTE_COLOR]
		CriteriaAggregate criteriaAgg = new CriteriaAggregate(orbType.id, "agg", attributeNames)
		
		criteria.addAnd(Constraint.are(attributeNames, Aggregate.NOT_AMONGST_UNIQUE, criteriaAgg))
		queryManager.addToCollection(criteria)
		
		Constraint constraintAgg = criteria.logicalConstraint.constraintList.get(0)
		
		assertNotNull(constraintAgg)
		assertTrue(constraintAgg instanceof ConstraintDetailsAggregate)
		
		Orb orb1 = new Orb()
		orb1.userDefinedProperties.put(QueryTestData.ATTRIBUTE_COLOR, 'bar')
		orb1.userDefinedProperties.put(QueryTestData.ATTRIBUTE_SIZE, 'big')
		Orb orb2 = new Orb()
		orb2.userDefinedProperties.put(QueryTestData.ATTRIBUTE_COLOR, 'barFoo')
		orb2.userDefinedProperties.put(QueryTestData.ATTRIBUTE_SIZE, 'small')
		
		Orb orb3 = new Orb()
		orb3.userDefinedProperties.put(QueryTestData.ATTRIBUTE_COLOR, 'barFoo')
		orb3.userDefinedProperties.put(QueryTestData.ATTRIBUTE_SIZE, 'small')
		
		OrbResultSet orbResultSet = new OrbResultSet([orb1, orb2] as ArrayList)
		
		when:
		Set<String> values = constraintProcessor.getAttributeValuesByFrequency(constraintAgg, orbResultSet, 1I)
		
		logger.info("Number of values in set: " + values.size())
		
		values.each { item ->
			logger.info item
		}
		
		then:
		values.size() == 2
		
	}
	
	def 'test getAttributeValuesByFrequency when 2 keys'() {
		
		given:
		String queryLabel = "fuzzyThings"
		Criteria criteria = new CriteriaStandard(orbType.id, queryLabel)
		
		String[] attributeNames = [QueryTestData.ATTRIBUTE_COLOR, QueryTestData.ATTRIBUTE_SIZE]
		CriteriaAggregate criteriaAgg = new CriteriaAggregate(orbType.id, "agg", attributeNames)
		
		criteria.addAnd(Constraint.are(attributeNames, Aggregate.NOT_AMONGST_UNIQUE, criteriaAgg))
		queryManager.addToCollection(criteria)
		
		Constraint constraintAgg = criteria.logicalConstraint.constraintList.get(0)
		
		assertNotNull(constraintAgg)
		assertTrue(constraintAgg instanceof ConstraintDetailsAggregate)
		
		Orb orb1 = new Orb()
		orb1.userDefinedProperties.put(QueryTestData.ATTRIBUTE_COLOR, 'barFoo')
		orb1.userDefinedProperties.put(QueryTestData.ATTRIBUTE_SIZE, 'big')
		
		Orb orb2 = new Orb()
		orb2.userDefinedProperties.put(QueryTestData.ATTRIBUTE_COLOR, 'barFoo')
		orb2.userDefinedProperties.put(QueryTestData.ATTRIBUTE_SIZE, 'small')
		
		Orb orb3 = new Orb()
		orb3.userDefinedProperties.put(QueryTestData.ATTRIBUTE_COLOR, 'barFoo')
		orb3.userDefinedProperties.put(QueryTestData.ATTRIBUTE_SIZE, 'small')
		
		OrbResultSet orbResultSet = new OrbResultSet([orb1, orb2] as ArrayList)
		
		when:
		Set<String> values = constraintProcessor.getAttributeValuesByFrequency(constraintAgg, orbResultSet, 1I)
		
		logger.info("Number of values in set: " + values.size())
		
		values.each { item ->
			logger.info item
		}
		
		then:
		values.size() == 2
		
	}
	
	Criteria extractAggregateCriteria(Criteria criteria) {
		Constraint constraintAgg = criteria.logicalConstraint.constraintList.get(0)
		
		assertNotNull(constraintAgg)
		assertTrue(constraintAgg instanceof ConstraintDetailsAggregate)
		
		return ((ConstraintDetailsAggregate) constraintAgg).criteriaForAggregation
	}
}
