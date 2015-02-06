package com.fletch22.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.rollback.RollbackAction;
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
	
	public void logAction(OperationResult operationResult) {
		
		StringBuilder undoAction = convertRollbackToJsonAction(operationResult.rollbackAction);
		
		if (operationResult.isIncludeInternalIdInLog()) {
			undoAction = logBundler.bundle(operationResult.action, operationResult.internalIdBeforeOperation);
		}
		
		this.logActionDao.logAction(operationResult.action, undoAction, operationResult.tranDate, operationResult.tranDate);
	}

	private StringBuilder convertRollbackToJsonAction(RollbackAction rollbackAction) {
		return new StringBuilder(gson.toJson(rollbackAction));
	}
}
