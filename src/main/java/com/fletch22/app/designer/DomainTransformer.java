package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolver;
import com.fletch22.orb.Orb;

public abstract class DomainTransformer {
	
	@Autowired
	ReferenceResolver referenceResolver;
	
	protected void setBaseAttributes(Orb orb, OrbBasedComponent orbBasedComponent) {
		orbBasedComponent.setOrbOriginal(orb);
		orbBasedComponent.setId(orb.getOrbInternalId());
	}
	
	protected void resolveChildren(OrbBasedComponent orbBasedComponent, String references, boolean isResolveAllChildren) {
		referenceResolver.resolve(orbBasedComponent, references, isResolveAllChildren);
	}
}
