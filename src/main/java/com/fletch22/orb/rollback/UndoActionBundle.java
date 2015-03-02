package com.fletch22.orb.rollback;

import java.math.BigDecimal;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class UndoActionBundle {
	
	Logger logger = LoggerFactory.getLogger(UndoActionBundle.class);
	
	private Stack<UndoAction> actions = new Stack<UndoAction>();

	public Stack<UndoAction> getActions() {
		return this.actions;
	}
	
	public void addAction(StringBuilder action, BigDecimal tranDate) {
		this.actions.push(new UndoAction(action, tranDate));
	}
	
	public StringBuilder toJson() {
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(this));
	}

	public void addAction(Stack<UndoAction> actions) {
		this.actions.addAll(actions);
	}

	public void clearUndoActions() {
		this.actions.clear();
	}
}
