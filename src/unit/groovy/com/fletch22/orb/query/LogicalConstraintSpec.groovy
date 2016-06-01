package com.fletch22.orb.query;

import static org.junit.Assert.*

import org.junit.Test
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.orb.query.constraint.CriteriaBuilder
import com.fletch22.orb.query.criteria.Criteria;

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class LogicalConstraintSpec extends Specification {
	
	LogicalConstraint logicalConstraint

	@Test
	public void test() {
		
		given:
		Criteria criteria = new CriteriaBuilder(123).addAmongstUniqueConstraint(123, "foo").build();
		
		when:
		StringBuffer description = criteria.getDescription();
		
		then:
		description != null
		print description.toString()	
	}
}
