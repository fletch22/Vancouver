package com.fletch22.orb.query;

import static org.junit.Assert.*
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.any

import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.Fletch22ApplicationContext
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.query.constraint.CriteriaBuilder
import com.fletch22.orb.query.criteria.CriteriaStandard
import com.fletch22.util.RandomUtil

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class LogicalConstraintSpec extends Specification {

	LogicalConstraint logicalConstraint
	ApplicationContext applicationContextOriginal;
	
	@Autowired
	RandomUtil randomUtil
	
	def setup() {
		
		this.applicationContextOriginal = Fletch22ApplicationContext.getApplicationContext()
		
		ApplicationContext mockApplicationContext = Mock(ApplicationContext)
		
		OrbTypeManager orbTypeManager = Mock(OrbTypeManager)
		OrbType orbType = new OrbType(234, 'bar', BigDecimal.ONE, new LinkedHashSet<String>())
		
		orbTypeManager.getOrbType(_ as Long) >> orbType
		
		mockApplicationContext.getBean({it == OrbTypeManager.class}) >> orbTypeManager
		
		assertNull(mockApplicationContext.getBean(RandomUtil.class.getName()))
		assertNotNull(this.randomUtil);
		mockApplicationContext.getBean({it == RandomUtil.class}) >> this.randomUtil
		
		assertNotNull(mockApplicationContext.getBean(RandomUtil.class))
		
		Fletch22ApplicationContext.setApplicationContext(mockApplicationContext)
	}

	def cleanup() {
		Fletch22ApplicationContext.setApplicationContext(this.applicationContextOriginal);
	}

	def test() {

		given:
		CriteriaBuilder criteriaBuilder = new CriteriaBuilder(123)
		criteriaBuilder.randomUtil = this.randomUtil
		
		CriteriaStandard criteriaStandard = criteriaBuilder.addAmongstUniqueConstraint(123, "foo").build();

		when:
		StringBuffer description = criteriaStandard.getDescription();

		then:
		description != null
		print description.toString()
		description.toString() == "AND the value(s) for attribute(s) [foo] ARE AMONGST_UNIQUE for Orb Type [bar]'s attribute(s) [foo] "
	}
}
