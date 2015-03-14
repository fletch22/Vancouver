package com.fletch22.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.rollback.UndoAction;
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
		
		StringBuilder undoAction = commandProcessActionPackage.getUndoActionBundle().toJson();
		
		StringBuilder action = operationResult.action;
		if (operationResult.isIncludeInternalIdInLog()) {
			action = logBundler.bundle(operationResult.action, operationResult.internalIdBeforeOperation);
		}
		
		this.logActionDao.logAction(action, undoAction, commandProcessActionPackage.getTranId(), commandProcessActionPackage.getTranDate());
	}
	
	public List<UndoActionBundle> getUndoActions(long tranId) {
		
		List<UndoActionBundle> actions = this.logActionDao.getUndosForTransactionAndSubesequentTransactions(tranId);
		
		Stack<UndoActionBundle> undoActionBundleStack = new Stack<UndoActionBundle>();
		for(int i = actions.size() - 1; i >= 0; i--) {
			undoActionBundleStack.add(actions.get(i));
		}
		
		return new ArrayList<UndoActionBundle>(undoActionBundleStack);
	}

	public void rollbackLog(long longValue) {
		this.logActionDao.rollbackLog(longValue);
	}
}
