package com.fletch22.orb.rollback;

import java.math.BigDecimal;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class UndoActionBundle {
	
	Logger logger = LoggerFactory.getLogger(UndoActionBundle.class);
	
	@Expose
	private Stack<UndoAction> actions = new Stack<UndoAction>();

	public Stack<UndoAction> getActions() {
		return this.actions;
	}
	
	public void addUndoAction(StringBuilder action, BigDecimal tranDate) {
		this.actions.push(new UndoAction(action, tranDate));
	}
	
	public StringBuilder toJson() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return new StringBuilder(gson.toJson(this));
	}
	
	public static UndoActionBundle fromJson(StringBuilder value) {
		Gson gson = new Gson();
		return gson.fromJson(value.toString(), UndoActionBundle.class);
	}

	public void addAction(Stack<UndoAction> actions) {
		this.actions.addAll(actions);
	}

	public void clearUndoActions() {
		this.actions.clear();
	}
}
