package com.fletch22.orb.transaction;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.rollback.UndoAction;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class RollbackService {
	
	Logger logger = LoggerFactory.getLogger(RollbackService.class);

	@Autowired
	LogActionService logActionService;
	
	@Autowired
	CommandProcessor commandProcessor;
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessPackageFactory;
	
	public void rollbackToSpecificTransaction(BigDecimal tranId) {
		
		List<UndoActionBundle> undoActionBundleList = logActionService.getUndoActions(tranId.longValue());
		
		OperationResult operationResult = null;
		for (UndoActionBundle undoActionBundle : undoActionBundleList) {
			
			UndoAction undoAction = undoActionBundle.getActions().pop();
			
			logger.info(undoAction.action.toString());
			
			CommandProcessActionPackage commandProcesActionPackage = this.commandProcessPackageFactory.getInstance(undoAction.action, undoAction.tranDate);
			commandProcesActionPackage.setIsInRestoreMode(true);
			
			operationResult = this.commandProcessor.processAction(commandProcesActionPackage);
			
			if (operationResult.opResult.equals(OpResult.FAILURE)) {
				logger.error(operationResult.operationResultException.getMessage());
				throw new RuntimeException("Encountered problem while trying to rollback database. Database is out of sync with cache. Consider re-initing from startup: ", operationResult.operationResultException);
			}
			
			this.logActionService.rollbackLog(tranId.longValue());
		}
		
	}
}
