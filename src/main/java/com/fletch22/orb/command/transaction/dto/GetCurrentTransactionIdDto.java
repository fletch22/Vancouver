package com.fletch22.orb.command.transaction.dto;

import java.math.BigDecimal;

public class GetCurrentTransactionIdDto {
	
	BigDecimal transactionId;

	public GetCurrentTransactionIdDto(BigDecimal transactionId) {
		this.transactionId = transactionId;
	}
}
