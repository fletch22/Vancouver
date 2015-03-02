package com.fletch22.orb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.processor.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;

@Component
public class OrbTypeService {

	Logger logger = LoggerFactory.getLogger(OrbTypeService.class);

	@Autowired
	CommandProcessor commandProcessor;

	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;

	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand;

	@Autowired
	TranDateGenerator tranDateGenerator;

	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;

	public long addOrbType(String label) {

		StringBuilder action = this.addOrbTypeCommand.toJson(label);

		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		commandProcessActionPackage.setAction(action);

		OperationResult operationResult = commandProcessor.processAction(commandProcessActionPackage);

		if (operationResult.opResult.equals(OpResult.SUCCESS)) {
			return (long) operationResult.operationResultObject;
		} else {
			throw new RuntimeException(operationResult.operationResultException);
		}
	}
	
	public void deleteOrbType(long orbInternalId, boolean allowCascadingDeletes) {
		StringBuilder action = this.deleteOrbTypeCommand.toJson(orbInternalId, allowCascadingDeletes);

		CommandProcessActionPackage commandProcessActionPackage = new CommandProcessActionPackage();
		commandProcessActionPackage.setAction(action);

		OperationResult operationResult = commandProcessor.processAction(commandProcessActionPackage);

		if (operationResult.opResult.equals(OpResult.FAILURE)) {
			throw new RuntimeException(operationResult.operationResultException);
		}
	}
}
