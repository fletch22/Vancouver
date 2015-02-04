package com.fletch22.orb.rollback;

import java.util.Stack;

import org.apache.commons.lang3.NotImplementedException;

public class RollbackAction {
	
	private Stack<StringBuilder> actions = new Stack<StringBuilder>();

	public RollbackAction fromJson(String json) {
		throw new NotImplementedException("Not yet implmplemented.");
	}
	
	public String toJson() {
		throw new NotImplementedException("Not yet implmplemented.");
	}

	public void addAction(StringBuilder action) {
		actions.push(action);
	}
	
	public Stack<StringBuilder> getActions() {
		return this.actions;
	}
}
