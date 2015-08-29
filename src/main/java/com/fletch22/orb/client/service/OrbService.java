package com.fletch22.orb.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.orb.AddOrbCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;

@Component
public class OrbService {

	Logger logger = LoggerFactory.getLogger(OrbService.class);

	@Autowired
	CommandProcessor commandProcessor;

	@Autowired
	AddOrbCommand addOrbCommand;

	@Autowired
	DeleteOrbTypeCommand deleteOrbCommand;

	@Autowired
	TranDateGenerator tranDateGenerator;

	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;

	public Orb addOrb(long orbTypeInternalId) {

		StringBuilder action = this.addOrbCommand.toJson(orbTypeInternalId);

		CommandProcessActionPackage commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(action);
		OperationResult operationResult = commandProcessor.processAction(commandProcessActionPackage);

		if (operationResult.opResult.equals(OpResult.SUCCESS)) {
			return (Orb) operationResult.operationResultObject;
		} else {
			throw new RuntimeException(operationResult.operationResultException);
		}
	}
	
	public void deleteOrb(long orbInternalId, boolean allowCascadingDeletes) {
		StringBuilder action = this.deleteOrbCommand.toJson(orbInternalId, allowCascadingDeletes);

		CommandProcessActionPackage commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(action);

		OperationResult operationResult = commandProcessor.processAction(commandProcessActionPackage);

		if (operationResult.opResult.equals(OpResult.FAILURE)) {
			throw new RuntimeException(operationResult.operationResultException);
		}
	}
}
