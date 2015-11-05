package com.fletch22.app.designer;

import java.util.ArrayList;

public class ComponentChildren {

	private ArrayList<OrbBasedComponent> children = new ArrayList<OrbBasedComponent>();
	private boolean haveChildrenBeenResolved = false;

	public ArrayList<OrbBasedComponent> list() {
		return children;
	}

	public boolean isHaveChildrenBeenResolved() {
		return haveChildrenBeenResolved;
	}

	public void setHaveChildrenBeenResolved(boolean haveChildrenBeenResolved) {
		this.haveChildrenBeenResolved = haveChildrenBeenResolved;
	}

	public void addChild(Child orbBasedComponent) {
		this.children.add((OrbBasedComponent) orbBasedComponent);
	}	
	
	public void clear() {
		this.children.clear();
	}
}
