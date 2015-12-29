package com.fletch22.dao;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.Dao.ActionInfo;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class LogActionService {
	
	Logger logger = LoggerFactory.getLogger(LogActionService.class);

	@Autowired
	LogActionDaoImpl logActionDao;

	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	@Autowired
	CommandProcessor commandProcessor;

	@Autowired
	LogBundler logBundler;

	public void logAction(OperationResult operationResult, CommandProcessActionPackage commandProcessActionPackage) {

		StringBuilder undoAction = commandProcessActionPackage.getUndoActionBundle().toJson();
		
		logger.debug("Undoing action: {}", undoAction);

		StringBuilder action = operationResult.action;
		if (operationResult.isIncludeInternalIdInLog()) {
			action = logBundler.bundle(operationResult.action, operationResult.internalIdBeforeOperation);
		}

		this.logActionDao.logAction(action, undoAction, commandProcessActionPackage.getTranId(), commandProcessActionPackage.getTranDate());
	}

	public List<UndoActionBundle> getUndoActions(BigDecimal tranId) {
		return this.logActionDao.getUndosForTransactionAndSubesequentTransactions(tranId);
	}

	public void loadCacheFromDb() {
		List<ActionInfo> actionInfoList = this.logActionDao.getAllActions(); 
		
		for (ActionInfo actionInfo: actionInfoList) {
			CommandProcessActionPackage commandProcessActionPackage = this.commandProcessActionPackageFactory.getInstanceForRestoreMode(actionInfo.action, actionInfo.tranDate);
			OperationResult operationResult = commandProcessor.processAction(commandProcessActionPackage);
			
			if (operationResult.opResult.equals(OpResult.FAILURE)) {
				throw new RuntimeException("Encountered problem reloading database into cache.", operationResult.operationResultException);
			}
		}
	}
	
	public void clearOutDatabase() {
		logActionDao.clearOutDatabase();
	}
}
