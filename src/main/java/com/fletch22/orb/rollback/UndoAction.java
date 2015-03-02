package com.fletch22.orb.rollback;

import java.math.BigDecimal;

public class UndoAction {
	
	public UndoAction(StringBuilder action, BigDecimal tranDate) {
		this.action = action;
		this.tranDate = tranDate;
	}
	
	public StringBuilder action;
	public BigDecimal tranDate;
}
