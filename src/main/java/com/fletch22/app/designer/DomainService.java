package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolverService;


public abstract class DomainService {
	
	@Autowired
	ReferenceResolverService referenceResolver;
	
	public void resolveAllDescendents(OrbBasedComponent orbBasedComponent) {
		referenceResolver.resolveAllDescendents(orbBasedComponent);
	}

	protected void connectParentAndChild(Parent parent, Child child) {
		child.setParentId(parent.getId());
		parent.getChildren().addChild(child);
	}
}
