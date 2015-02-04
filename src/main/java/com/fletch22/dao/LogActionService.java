package com.fletch22.dao;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.rollback.RollbackAction;

@Component
public class LogActionService {

	@Autowired
	LogActionDao logActionDao;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	public void logAction(OperationResult operationResult) {
		
		StringBuilder undoAction = convertRollbackToJson(operationResult.rollbackAction);
		
		this.logActionDao.logAction(operationResult.action, undoAction, operationResult.tranDate, operationResult.tranDate);
		
	}

	private StringBuilder convertRollbackToJson(RollbackAction rollbackAction) {
		throw new NotImplementedException("Not implemented.");
	}
}
