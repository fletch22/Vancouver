package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	
	public void sort() {
		Collections.sort(this.children, new Comparator<Child>() {
	        @Override public int compare(Child p1, Child p2) {
	        	int result = 0;
	        	if (p1.getId() > p2.getId())  {
	        		result = 1;
	        	} else if (p1.getId() < p2.getId()) {
	        		result = -1;
	        	}
	            return result; 
	        }
	    });
	}
}
