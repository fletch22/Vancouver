package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolver;


public abstract class DomainService {
	
	@Autowired
	ReferenceResolver referenceResolver;
	
	public void resolveChildren(OrbBasedComponent orbBasedComponent, String references) {
		referenceResolver.resolve(orbBasedComponent, references);
	}
}
