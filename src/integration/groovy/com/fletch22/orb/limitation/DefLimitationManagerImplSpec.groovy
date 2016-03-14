package com.fletch22.orb.limitation;

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
import com.fletch22.orb.client.service.BeginTransactionService
import com.fletch22.orb.command.transaction.RollbackTransactionService
import com.fletch22.orb.query.Criteria
import com.fletch22.orb.query.CriteriaAggregate
import com.fletch22.orb.query.CriteriaStandard
import com.fletch22.orb.query.constraint.Constraint
import com.fletch22.orb.query.constraint.aggregate.Aggregate

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class DefLimitationManagerImplSpec extends Specification {

	@Autowired
	DefLimitationManager defLimitationManager

	@Autowired
	OrbTypeManager orbTypeManager

	@Autowired
	OrbManager orbManager

	@Autowired
	BeginTransactionService beginTransactionService

	@Autowired
	RollbackTransactionService rollbackTransactionService

	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer

	def setup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	def cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems()
	}

	def 'test add default criteria success'() {

		given:
		long orbTypeInternalId = orbTypeManager.createOrbType("asdf", null);

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		Criteria criteria = new CriteriaStandard(orbType.id, "howdy")

		when:
		defLimitationManager.addToCollection(criteria)

		then:
		defLimitationManager.doesCriteriaExist(criteria.getCriteriaId())
	}

	def 'test  and rollback'() {

		given:
		def tranId = beginTransactionService.beginTransaction()
		long orbTypeInternalId = orbTypeManager.createOrbType("asdf", null);

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		Criteria criteria = new CriteriaStandard(orbType.id, "howdy")

		defLimitationManager.addToCollection(criteria)

		when:
		rollbackTransactionService.rollbackToBeforeSpecificTransaction(tranId)

		then:
		!defLimitationManager.doesCriteriaExist(criteria.getCriteriaId())
	}

	def 'test criteria unique'() {

		given:
		String attributeColorName = "color";
		def tranId = beginTransactionService.beginTransaction()
		long orbTypeInternalId = orbTypeManager.createOrbType("asdf", null);

		orbTypeManager.addAttribute(orbTypeInternalId, attributeColorName);

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)

		Criteria criteria = new CriteriaStandard(orbType.id, "foo1")

		CriteriaAggregate criteriaForAggregation = new CriteriaAggregate(orbType.id, "foo2", attributeColorName)
		criteria.addAnd(Constraint.is(attributeColorName, Aggregate.AMONGST_UNIQUE, criteriaForAggregation))
		defLimitationManager.addToCollection(criteria);

		Orb orb = orbManager.createOrb(orbTypeInternalId)
		orbManager.setAttribute(orb.getOrbInternalId(), attributeColorName, "red");
		
		when:
		orb = orbManager.createOrb(orbTypeInternalId)
		orbManager.setAttribute(orb.getOrbInternalId(), attributeColorName, "red");

		then:
		thrown Exception
	}

	def 'test dry run criteria'() {

		given:
		String attributeColorName = "color";
		long orbTypeInternalId = orbTypeManager.createOrbType("asdf", null);
		orbTypeManager.addAttribute(orbTypeInternalId, attributeColorName);

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		Orb orb = orbManager.createOrb(orbTypeInternalId)
		orbManager.setAttribute(orb.getOrbInternalId(), attributeColorName, "red");

		orb = orbManager.createOrb(orbTypeInternalId)
		orbManager.setAttribute(orb.getOrbInternalId(), attributeColorName, "red");
		
		orb = orbManager.createOrb(orbTypeInternalId)
		orbManager.setAttribute(orb.getOrbInternalId(), attributeColorName, "orange");

		Criteria criteria = new CriteriaStandard(orbType.id, "foo1")

		CriteriaAggregate criteriaForAggregation = new CriteriaAggregate(orbType.id, "foo2", attributeColorName)
		criteria.addAnd(Constraint.is(attributeColorName, Aggregate.NOT_AMONGST_UNIQUE, criteriaForAggregation))

		when:
		defLimitationManager.addToCollectionWithPreCheckConstraint(criteria);
		
		then:
		thrown Exception
	}
	
	def 'test dry run criteria when no records'() {
		
		given:
		String attributeColorName = "color";
		long orbTypeInternalId = orbTypeManager.createOrbType("asdf", null);
		orbTypeManager.addAttribute(orbTypeInternalId, attributeColorName);

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId)
		
		Criteria criteria = new CriteriaStandard(orbType.id, "foo1")

		CriteriaAggregate criteriaForAggregation = new CriteriaAggregate(orbType.id, "foo2", attributeColorName)
		criteria.addAnd(Constraint.is(attributeColorName, Aggregate.NOT_AMONGST_UNIQUE, criteriaForAggregation))

		when:
		defLimitationManager.addToCollectionWithPreCheckConstraint(criteria);
		
		then:
		notThrown(Exception)
	}
}
