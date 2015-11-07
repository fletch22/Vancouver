package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import com.fletch22.app.designer.reference.ReferenceResolverService;

public abstract class DomainService<T extends Parent, U extends Child> {
	
	@Autowired
	ReferenceResolverService referenceResolver;
	
	public void addToParent(T t, U child) {
		connectParentAndChild(t, child);
		save(t);
	}
	
	public abstract void save(T t);
	
	public void clearAndResolveAllDescendents(Parent orbBasedComponent) {
		referenceResolver.clearAResolveAllDescendents(orbBasedComponent);
	}
	
	public void clearAndResolveNextGeneration(Parent orbBasedComponent) {
		referenceResolver.clearAndResolveNextGeneration(orbBasedComponent);
	}

	protected void connectParentAndChild(T parent, U child) {
		clearAndResolveNextGeneration(parent);
		child.setParentId(parent.getId());
		parent.getChildren().addChild(child);
	}
}
