package com.fletch22.orb.command.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.transaction.UndoService;

@Component
public class RedoAndUndoLogging {
	
	@Autowired
	LogActionService logActionService;
	
	@Autowired
	UndoService undoService;

	public void logRedoAndUndo(CommandProcessActionPackage commandProcessActionPackage, OperationResult operationResult) {
		try {
			log(commandProcessActionPackage, operationResult);
		} catch (Exception e) {
			e.printStackTrace();
			Exception explained = new Exception("Orb DB and SQL DB are now out of sync. Restart is recommended.", e);
			throw new RuntimeException(explained);
		}
	}

	private void log(CommandProcessActionPackage commandProcessActionPackage, OperationResult operationResult) {
		if (operationResult.shouldBeLogged && !commandProcessActionPackage.isInRestoreMode() && operationResult.opResult == OpResult.SUCCESS) {
			logActionService.logAction(operationResult, commandProcessActionPackage);
		}

		if (operationResult.opResult != OpResult.SUCCESS) {
			this.undoService.undoActions(commandProcessActionPackage.getUndoActionBundle());
		}
	}
}
