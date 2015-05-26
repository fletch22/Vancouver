package com.fletch22.orb.system

import org.joda.time.DateTime
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.IntegrationTests
import com.fletch22.orb.TranDateGenerator
import com.fletch22.util.NowFactory

class TranDateGeneratorSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(TranDateGeneratorSpec)

	TranDateGenerator tranDateGenerator
	
	@Shared
	NowFactory nowFactory
	
	@Shared
	DateTime now
	
	def setup() {
		tranDateGenerator = new TranDateGenerator();
		
		now = DateTime.now()
		
		nowFactory = Mock(NowFactory)
		
		nowFactory.getNow() >> now
		
		tranDateGenerator.nowFactory = nowFactory
	}
	
	@Test
	def 'testConvert'() {
		
		given:
		BigDecimal currentTranDate = tranDateGenerator.getCurrentTranDate()
		
		when:
		DateTime dateTimeCurrent = tranDateGenerator.convertToNearestMillisecond(currentTranDate)
		String dateString = String.valueOf(currentTranDate);
		
		then:
		dateTimeCurrent == now
	}
}
