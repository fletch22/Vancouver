package com.fletch22.app.designer;

import java.util.ArrayList;

public class ComponentChildren {

	private ArrayList<Child> children = new ArrayList<Child>();
	private boolean haveChildrenBeenResolved = false;

	public ArrayList<Child> getList() {
		return children;
	}

	public boolean isHaveChildrenBeenResolved() {
		return haveChildrenBeenResolved;
	}

	public void setHaveChildrenBeenResolved(boolean haveChildrenBeenResolved) {
		this.haveChildrenBeenResolved = haveChildrenBeenResolved;
	}

	public void addChild(Child child) {
		this.children.add(child);
	}	
	
	public void clear() {
		this.children.clear();
	}
}
