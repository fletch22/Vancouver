package com.fletch22.app.designer;

import com.fletch22.orb.Orb;

public abstract class DomainTransformer<T extends OrbBasedComponent> {
	
	protected void setBaseAttributes(Orb orb, OrbBasedComponent orbBasedComponent) {
		orbBasedComponent.setOrbOriginal(orb);
		orbBasedComponent.setId(orb.getOrbInternalId());
	}
	
	public abstract T transform(Orb orb);
}
