package com.fletch22.orb.rollback;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

public class UndoAction {
	
	@Expose
	public StringBuilder action;
	
	@Expose
	public BigDecimal tranDate;
	
	public UndoAction(StringBuilder action, BigDecimal tranDate) {
		this.action = action;
		this.tranDate = tranDate;
	}
}
