package com.fletch22.orb.limitation;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.client.service.BeginTransactionService;
import com.fletch22.orb.command.transaction.RollbackTransactionService;
import com.fletch22.orb.query.CriteriaFactory
import com.fletch22.orb.query.CriteriaFactory.Criteria

@org.junit.experimental.categories.Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
class DefLimitationManagerImplSpec extends Specification {

	@Autowired
	DefLimitationManager defLimitationManager

	@Autowired
	CriteriaFactory criteriaFactory

	@Autowired
	OrbTypeManager orbTypeManager

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

		Criteria criteria = criteriaFactory.createInstance(orbType, "howdy")

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

		Criteria criteria = criteriaFactory.createInstance(orbType, "howdy")

		defLimitationManager.addToCollection(criteria)

		when:
		rollbackTransactionService.rollbackToSpecificTransaction(tranId)

		then:
		!defLimitationManager.doesCriteriaExist(criteria.getCriteriaId())
	}
}
