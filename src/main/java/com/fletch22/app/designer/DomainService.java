package com.fletch22.app.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolverService;

public abstract class DomainService<T extends Parent, U extends Child> {
	
	Logger logger = LoggerFactory.getLogger(DomainService.class);
	
	@Autowired
	ReferenceResolverService referenceResolver;
	
	public void addToParent(T parent, U child) {
		connectParentAndChild(parent, child);
		save(parent);
	}
	
	public abstract void save(T t);
	
	public abstract T get(long id);
	
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
