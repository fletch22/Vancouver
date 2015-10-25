package com.fletch22.orb;

import static org.junit.Assert.*

import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class TranDateGeneratorSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(TranDateGeneratorSpec)

	@Autowired
	TranDateGenerator tranDateGenerator
	
	@Unroll
	def 'test generator'() {
		
		given:
		when:
		def tranDate1 = tranDateGenerator.getTranDate();
		def tranDate2 = tranDateGenerator.getTranDate();
		
		then:
		tranDate1 != TranDateGenerator.TRAN_DATE_UNSET
		tranDate1 != tranDate2
		
		tranDate1 != null
	}
	
	@Unroll
	def 'test generator multiple'() {
		
		given:
		Set<Long> tranDateList = new HashSet<Long>()
		
		when:
		logger.debug("Start")
		1000.times {
			def tranDate = tranDateGenerator.getTranDate()
		}
		logger.debug("Stop")
		
		def etst = ''
		then:
		1 == 1
	}
	
	@Unroll
	def 'test time resets generator'() {
		
		given:
		BigDecimal firstTranDate = tranDateGenerator.getCurrentTranDate()
		
		Thread.sleep(3)
		
		BigDecimal secondTranDate = tranDateGenerator.getCurrentTranDate()
		
		tranDateGenerator.@lastTranDateRaw = secondTranDate
		
		when:
		def finalTranDate = tranDateGenerator.ensureGoodLogDate(firstTranDate);
		
		then:
		finalTranDate.compareTo(tranDateGenerator.@lastTranDateRaw) > 0
		logger.debug("final: {}", String.format("%10.10f%n", finalTranDate));
		logger.debug("lastOne: {}", String.format("%10.10f%n", tranDateGenerator.@lastTranDateRaw));
	}
	
	@Unroll
	def 'test time resets generator exceeds max'() {
		
		given:
		tranDateGenerator.@lastTranDateRaw = tranDateGenerator.getCurrentTranDate()
		
		Thread.sleep(3)
		
		long rollbackMillis = new DateTime(tranDateGenerator.@lastTranDateRaw.longValue()).minusSeconds(10).getMillis()
		
		logger.debug("LTDR: {}", tranDateGenerator.@lastTranDateRaw.longValue());
		logger.debug("RM: {}", rollbackMillis);
		
		when:
		def finalTranDate = tranDateGenerator.ensureGoodLogDate(new BigDecimal(rollbackMillis))
		
		then:
		RuntimeException ex = thrown()
	}
}
