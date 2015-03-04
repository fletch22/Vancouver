package com.fletch22.orb.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.rollback.UndoAction;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
public class UndoService {
	
	@Autowired
	CommandProcessor commandProcessor;
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;

	public void undoActions(UndoActionBundle undoActionBundle) {
		
		while (!undoActionBundle.getActions().empty()) {
			UndoAction undoAction = undoActionBundle.getActions().pop();
			
			CommandProcessActionPackage commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(undoAction.action, undoAction.tranDate);
			
			OperationResult operationResult = this.commandProcessor.executeAction(commandProcessActionPackage);
			
			if (operationResult.opResult == OpResult.FAILURE) {
				throw new RuntimeException("Encountered problem while trying to undo actions. Log and Cache are out of sync. Consider rolling back or re-initing from full log.");
			}
		}
	}
}
