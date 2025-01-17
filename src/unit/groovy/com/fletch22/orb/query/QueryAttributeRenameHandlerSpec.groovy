package com.fletch22.orb.query;

import static org.junit.Assert.*
import spock.lang.Specification

import com.fletch22.orb.OrbType
import com.fletch22.orb.query.constraint.Constraint
import com.fletch22.orb.query.constraint.ConstraintDetailsSingleValue
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaAttributeRenameHandler;
import com.fletch22.orb.query.criteria.CriteriaStandard;
import com.fletch22.orb.query.sort.CriteriaSortInfo

class QueryAttributeRenameHandlerSpec extends Specification {
	
	static final String ORIGINAL_ATTRIBUTE_NAME = "foo"
	static final String ORIGINAL_QUERY_NAME = "TheGreatestQueryNameEvah"

	def 'test attribute rename in constraint'() {
		
		given:
		CriteriaAttributeRenameHandler queryAttributeRenameHandler = new CriteriaAttributeRenameHandler()
		
		OrbType orbType = Mock(OrbType)
		
		Criteria criteria = new CriteriaStandard(orbType.id, ORIGINAL_QUERY_NAME)
		
		ConstraintDetailsSingleValue constraintDetailSingleValue = (ConstraintDetailsSingleValue) Constraint.eq(ORIGINAL_ATTRIBUTE_NAME, "someValue")
		
		criteria.addAnd(constraintDetailSingleValue)
		
		when:
		queryAttributeRenameHandler.renameInConstraints(criteria, ORIGINAL_ATTRIBUTE_NAME, "Bar")
		
		then:
		queryAttributeRenameHandler
		constraintDetailSingleValue.attributeName == "Bar"
	}
	
	def 'test attribute rename in sort info'() {
		
		given:
		CriteriaAttributeRenameHandler queryAttributeRenameHandler = new CriteriaAttributeRenameHandler()
		
		OrbType orbType = Mock(OrbType)
		
		Criteria criteria = new CriteriaStandard(orbType.id, ORIGINAL_QUERY_NAME)
		
		ConstraintDetailsSingleValue constraintDetailSingleValue = (ConstraintDetailsSingleValue) Constraint.eq(ORIGINAL_ATTRIBUTE_NAME, "someValue")
		
		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo()
		criteriaSortInfo.sortAttributeName = ORIGINAL_ATTRIBUTE_NAME
		criteria.setSortOrder()
		
		criteria.addAnd(constraintDetailSingleValue)
		
		when:
		queryAttributeRenameHandler.renameInConstraints(criteria, ORIGINAL_ATTRIBUTE_NAME, "Bar")
		
		then:
		queryAttributeRenameHandler
		constraintDetailSingleValue.attributeName == "Bar"
	}
}
