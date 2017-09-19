package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

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
	
	public void removeChild(Child child) {
		validateChildrenResolved();
		
		if (!this.children.contains(child)) {
			throw new RuntimeException("Encountered problem while trying to remove child. Child is not in parent.");
		}
		
		this.children.remove(child);
	}
	
	public Child findChildById(long childId) {
		validateChildrenResolved();
		
		Optional<Child> childFound = this.children.stream()
		.filter(child -> (child.getId() == childId))
		.collect(Collectors.reducing((a, b) -> {
			throw new RuntimeException("Encountered problem while trying to remove child. More than one child with that ID found in parent.");
		}));
	
		if (!childFound.isPresent()) {
			throw new RuntimeException(String.format("Encountered problem while trying to find child. Child with id %s could not be found in parent.", childId)); 
		}
		
		return childFound.get();
	}
	
	public void validateChildrenResolved() {
		if (!haveChildrenBeenResolved) {
			throw new RuntimeException("Encountered problem while trying to remove child. Child is not in parent because children have not been 'resolved'. This is a programming error.");
		}
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
