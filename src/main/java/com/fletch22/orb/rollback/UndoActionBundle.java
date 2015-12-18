package com.fletch22.orb.rollback;

import java.math.BigDecimal;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.util.json.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class UndoActionBundle {
	
	transient Logger logger = LoggerFactory.getLogger(UndoActionBundle.class);
	
	@Expose
	private Stack<UndoAction> actions = new Stack<UndoAction>();

	public Stack<UndoAction> getActions() {
		return this.actions;
	}
	
	public void addUndoAction(StringBuilder action, BigDecimal tranDate) {
		this.actions.push(new UndoAction(action, tranDate));
	}
	
	public StringBuilder toJson() {
		return new StringBuilder(getGson().toJson(this));
	}
	
	public static UndoActionBundle fromJson(StringBuilder value) {
		return getGson().fromJson(value.toString(), UndoActionBundle.class);
	}

	public void addAction(Stack<UndoAction> actions) {
		this.actions.addAll(actions);
	}

	public void clearUndoActions() {
		this.actions.clear();
	}
	
	public static Gson getGson() {
		return Fletch22ApplicationContext.getApplicationContext().getBean(GsonFactory.class).getInstance();
	}
}
