package com.fletch22.orb.rollback;

import java.util.Stack;

import com.google.gson.Gson;

public class RollbackAction {
	
	private Stack<StringBuilder> actions = new Stack<StringBuilder>();

	public Stack<StringBuilder> getActions() {
		return this.actions;
	}
	
	public void addAction(StringBuilder action) {
		this.actions.push(action);
	}
	
	public StringBuilder toJson() {
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(this));
	}
}
