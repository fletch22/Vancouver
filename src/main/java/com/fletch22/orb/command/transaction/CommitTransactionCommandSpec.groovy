package com.fletch22.orb.command.transaction;

import static org.junit.Assert.*;

import org.junit.Test;

import spock.lang.Specification;

class CommitTransactionCommandSpec extends Specification {

	@Test
	def 'test serialize and deserialize'() {
		
		given:
		CommitTransactionCommand commitTransactionCommand = new CommitTransactionCommand()
		
		def bigDecimalExpected = new BigDecimal(123)
		StringBuilder sb = commitTransactionCommand.toJson(bigDecimalExpected)
		
		when:
		def commitTransactionDto = commitTransactionCommand.fromJson(sb.toString())
		
		then:
		commitTransactionDto
		commitTransactionDto.tranId == bigDecimalExpected
	}

}
