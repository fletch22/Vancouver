package com.fletch22.orb.command.transaction.dto;

import java.math.BigDecimal;

public class KillTransactionDto {

	public BigDecimal transactionId;
	
	public KillTransactionDto(BigDecimal transactionId) {
		this.transactionId = transactionId;
	}
}
