package com.fletch22.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.command.processor.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.google.gson.Gson;

@Component
public class LogActionService {

	@Autowired
	LogActionDao logActionDao;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	LogBundler logBundler;
	
	Gson gson = new Gson();
	
	public void logAction(OperationResult operationResult, CommandProcessActionPackage commandProcessActionPackage) {
		
		StringBuilder undoAction = convertRollbackToJsonAction(commandProcessActionPackage.getUndoActionBundle());
		
		if (operationResult.isIncludeInternalIdInLog()) {
			undoAction = logBundler.bundle(operationResult.action, operationResult.internalIdBeforeOperation);
		}
		
		this.logActionDao.logAction(operationResult.action, undoAction, commandProcessActionPackage.getTranId(), commandProcessActionPackage.getTranDate());
	}

	private StringBuilder convertRollbackToJsonAction(UndoActionBundle rollbackAction) {
		return new StringBuilder(gson.toJson(rollbackAction));
	}
	
	public UndoActionBundle getUndoActions(long tranId) {
	
		
		return null;
	}
}
