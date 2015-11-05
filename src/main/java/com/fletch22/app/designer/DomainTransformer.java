package com.fletch22.app.designer;

import com.fletch22.orb.Orb;

public abstract class DomainTransformer {
	
	protected void setBaseAttributes(Orb orb, OrbBasedComponent orbBasedComponent) {
		orbBasedComponent.setOrbOriginal(orb);
		orbBasedComponent.setId(orb.getOrbInternalId());
	}
}
