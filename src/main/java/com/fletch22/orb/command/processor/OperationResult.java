package com.fletch22.orb.command.processor;

import java.math.BigDecimal;

import com.fletch22.orb.rollback.RollbackAction;

public class OperationResult {
	public enum OpResult 
	{
		SUCCESS,
		IN_THE_MIDDLE,
		FAILURE
	}

    public enum OperationResultReasonCode {
        UNASSIGNED,
        TIMEOUT,
        ROLLBACK_OCCURRING
    }
    
    public static final int UNSET = -1;
    public static OperationResult SUCCESS = new OperationResult(OpResult.SUCCESS);
	public static OperationResult IN_THE_MIDDLE = new OperationResult(OpResult.IN_THE_MIDDLE);
	public static OperationResult FAILURE = new OperationResult(OpResult.FAILURE);
	
	public OpResult opResult;
	public boolean shouldBeLogged = false;
    public long internalIdBeforeOperation = UNSET;
	public long internalIdAfterOperation = UNSET;
	public RollbackAction rollbackAction;
	
	public Object operationResultObject = null;
	public Exception operationResultException;
	
	public OperationResultReasonCode resultReasonCode = OperationResultReasonCode.UNASSIGNED;
	public StringBuilder action;
	public BigDecimal tranDate;

	public OperationResult(OpResult opResult)
	{
		this.opResult = opResult;
	}
	
	public OperationResult(OpResult opResult, Exception operationResultException)
	{
		this.opResult = opResult;
		this.operationResultException = operationResultException;
	}
	
	public OperationResult(OpResult opResult, boolean shouldBeLogged)
	{
		this.opResult = opResult;
		this.shouldBeLogged = shouldBeLogged;
	}
   
    public OperationResult(OpResult opResult, Object operationResultObject, boolean shouldBeLogged)
    {
        this.opResult = opResult;
        this.shouldBeLogged = shouldBeLogged;
        this.operationResultObject = operationResultObject;
    }
    
    public boolean isIncludeInternalIdInLog()
    {
        return (this.internalIdBeforeOperation != this.internalIdAfterOperation);
    }
}
