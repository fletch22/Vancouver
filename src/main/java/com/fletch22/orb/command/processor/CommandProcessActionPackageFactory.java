package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class CommandProcessActionPackageFactory {
	
	@Autowired
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	TransactionService transactionService;

	public CommandProcessActionPackage getInstance(StringBuilder action) {
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		
		BigDecimal tranDate = tranDateGenerator.getTranDate();
		commandProcessActionPackage.setAction(action)
		.setTranDate(tranDate)
		.setTranId(tranDate)
		.setUndoActionBundle(new UndoActionBundle());
		
		return commandProcessActionPackage;
	}
	
	public CommandProcessActionPackage getInstance(StringBuilder action, BigDecimal tranDate) {
		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		
		commandProcessActionPackage.setAction(action)
		.setTranDate(tranDate)
		.setTranId(tranDate)
		.setUndoActionBundle(new UndoActionBundle());
		
		return commandProcessActionPackage;
	}
	
	public static class CommandProcessActionPackage {
		private StringBuilder action;
		private BigDecimal tranDate;
		private BigDecimal tranId = null;
		private boolean isInRestoreMode = false;
		private UndoActionBundle undoActionBundle = new UndoActionBundle();
		
		private CommandProcessActionPackage() {}
		
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
}
