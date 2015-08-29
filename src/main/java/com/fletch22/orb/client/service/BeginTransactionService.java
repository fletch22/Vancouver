package com.fletch22.orb.client.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.command.processor.CommandProcessor;
import com.fletch22.orb.command.processor.OperationResult;
import com.fletch22.orb.command.processor.OperationResult.OpResult;
import com.fletch22.orb.command.transaction.BeginTransactionCommand;

@Component
public class BeginTransactionService {

	Logger logger = LoggerFactory.getLogger(OrbService.class);
	
	@Autowired
	BeginTransactionCommand beginTransactionCommand;
	
	@Autowired
	CommandProcessor commandProcessor;
	
	@Autowired
	CommandProcessActionPackageFactory commandProcessActionPackageFactory;
	
	public BigDecimal beginTransaction() {
		StringBuilder action = this.beginTransactionCommand.toJson();

		CommandProcessActionPackage commandProcessActionPackage = commandProcessActionPackageFactory.getInstance(action);
		OperationResult operationResult = commandProcessor.processAction(commandProcessActionPackage);

		if (operationResult.opResult.equals(OpResult.SUCCESS)) {
			return (BigDecimal) operationResult.operationResultObject;
		} else {
			throw new RuntimeException(operationResult.operationResultException);
		}
	}
	
}
