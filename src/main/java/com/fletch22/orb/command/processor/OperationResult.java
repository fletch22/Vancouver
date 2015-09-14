package com.fletch22.orb.command.processor;

public class OperationResult {
	public enum OpResult 
	{
		SUCCESS,
		IN_THE_MIDDLE,
		FAILURE
	}

    public static final int UNSET = -1;
    
    public static OperationResult getInstanceSuccess() {
    	return new OperationResult(OpResult.SUCCESS);
    }
    
    public static OperationResult getInstanceInTheMiddle() {
    	return new OperationResult(OpResult.IN_THE_MIDDLE);
    }
    
    public static OperationResult getInstanceFailure() {
    	return new OperationResult(OpResult.FAILURE);
    }

   public OpResult opResult;
	public boolean shouldBeLogged = false;
    public long internalIdBeforeOperation = UNSET;
	public long internalIdAfterOperation = UNSET;
	
	public Object operationResultObject = null;
	public Exception operationResultException;
	
	public StringBuilder action;

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
