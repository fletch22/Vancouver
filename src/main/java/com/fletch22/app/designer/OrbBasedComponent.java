package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class OrbBasedComponent {
	
	public static LinkedHashSet<String> ATTRIBUTE_LIST;
	
	private long id;
	private ArrayList<? extends OrbBasedComponent> children;
	
	public long getId() {
		return this.id;
	}
	
	public ArrayList<? extends OrbBasedComponent> getChildren() {
		return this.children;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
