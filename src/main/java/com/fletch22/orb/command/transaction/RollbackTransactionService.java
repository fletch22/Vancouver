package com.fletch22.orb.command.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.dao.LogActionDao.TransactionSearchResult;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.logging.EventLogCommandProcessPackageHolder;
import com.fletch22.orb.rollback.UndoAction;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class RollbackTransactionService {
	
	Logger logger = LoggerFactory.getLogger(RollbackTransactionService.class);

	@Autowired
	LogActionService logActionService;
	
	@Autowired
	CommandProcessor commandProcessor;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessPackageFactory;
	
	@Autowired
	EventLogCommandProcessPackageHolder eventLogCommandProcessPackageHolder;
	
	public void rollbackToBeforeSpecificTransaction(BigDecimal tranId) {
		List<UndoActionBundle> undoActionBundleList = logActionService.getUndoActionsForTransactionsAndSubsequent(tranId);
		
		executeUndoActions(undoActionBundleList);
		
		this.transactionService.rollbackToBeforeSpecificTransaction(tranId);
		
		eventLogCommandProcessPackageHolder.cleanup();
	}
	
	public void rollbackToSpecificTransaction(BigDecimal tranId) {
		Optional<BigDecimal> tranIdFound = this.logActionService.getSubsequentTranIdIfAny(tranId);

		if (tranIdFound.isPresent()) {
			this.rollbackToBeforeSpecificTransaction(tranIdFound.get());
		}
	}
	
	private void executeUndoActions(List<UndoActionBundle> undoActionBundleList) {
		OperationResult operationResult = null;
		for (UndoActionBundle undoActionBundle : undoActionBundleList) {
			
			while (undoActionBundle.getActions().size() > 0) {
				UndoAction undoAction = undoActionBundle.getActions().pop();
				
				logger.debug("Undoing action: {}", undoAction.action);
				
				CommandProcessActionPackage commandProcesActionPackage = this.commandProcessPackageFactory.getInstanceForRestoreMode(undoAction.action, undoAction.tranDate);
				
				operationResult = this.commandProcessor.processAction(commandProcesActionPackage);
				
				if (operationResult.opResult.equals(OpResult.FAILURE)) {
					logger.error(operationResult.operationResultException.getMessage());
					throw new RuntimeException("Encountered problem while trying to rollback database. Database is out of sync with cache. Consider re-initing from startup: ", operationResult.operationResultException);
				}
			}
		}
	}

	public void rollbackCurrentTransaction() {
		BigDecimal tranId = transactionService.getCurrentTransactionId();
		
		rollbackToBeforeSpecificTransaction(tranId);
	}
}
