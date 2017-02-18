package com.fletch22.app.designer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolverService;

public abstract class DomainService<T extends Parent, U extends Child> extends DomainServiceBase<T> {
	
	Logger logger = LoggerFactory.getLogger(DomainService.class);
	
	@Autowired
	ReferenceResolverService referenceResolver;
	
	@Override
	public abstract void save(T t);
	
	@Override
	public abstract T get(long id);
	
	public void addToParent(T parent, U child) {
		connectParentAndChild(parent, child);
		save(parent);
	}
		
	public void clearAndResolveAllDescendents(Parent orbBasedComponent) {
		referenceResolver.clearAndResolveAllDescendents(orbBasedComponent);
	}
	
	public void clearAndResolveNextGeneration(Parent orbBasedComponent) {
		referenceResolver.clearAndResolveNextGeneration(orbBasedComponent);
	}

	protected void connectParentAndChild(T parent, U child) {
		clearAndResolveNextGeneration(parent);
		child.setParentId(parent.getId());
		parent.getChildren().addChild(child);
	}
	
	public abstract T createInstance(Map<String, String> props);
	
	public abstract T update(long id, Map<String, String> properties);
}
