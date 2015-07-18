package com.fletch22.orb.command.orbType.dto;


public class MethodCallDto {
	
	public String className;
	public String methodName;
	public String[] parameterTypes;
	public Object[] args;
	
	public MethodCallDto(String className, String methodName, String[] parameterTypes, Object[] args) {
		this.className = className;
		this.methodName = methodName;
		this.args = args;
		this.parameterTypes = parameterTypes;
	}
}
