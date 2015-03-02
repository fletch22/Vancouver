package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import com.fletch22.orb.rollback.UndoActionBundle;

public class CommandProcessActionPackage {
	private StringBuilder action;
	private BigDecimal tranDate;
	private BigDecimal tranId;
	private boolean isInRestoreMode = false;
	private UndoActionBundle undoActionBundle = new UndoActionBundle();
	
	public CommandProcessActionPackage setAction(StringBuilder action) {
		this.action = action;
		return this;
	}
	
	public StringBuilder getAction() {
		return this.action;
	}
	
	public CommandProcessActionPackage setTranDate(BigDecimal tranDate) {
		this.tranDate = tranDate;
		return this;
	}
	
	public BigDecimal getTranDate() {
		return this.tranDate;
	}
	
	public CommandProcessActionPackage setIsInRestoreMode(boolean isInRestoreMode) {
		this.isInRestoreMode = isInRestoreMode;
		return this;
	}
	
	public boolean isInRestoreMode() {
		return this.isInRestoreMode;
	}

	public CommandProcessActionPackage setUndoActionBundle(UndoActionBundle undoActionBundle) {
		this.undoActionBundle = undoActionBundle;
		return this;
	}
	
	public UndoActionBundle getUndoActionBundle() {
		return this.undoActionBundle;
	}

	public BigDecimal getTranId() {
		return tranId;
	}

	public CommandProcessActionPackage setTranId(BigDecimal tranId) {
		this.tranId = tranId;
		return this;
	}
}
