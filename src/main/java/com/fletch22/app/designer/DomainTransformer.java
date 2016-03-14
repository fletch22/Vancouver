package com.fletch22.app.designer;

import com.fletch22.orb.Orb;

public abstract class DomainTransformer<T extends OrbBasedComponent> {
	
	protected void setBaseAttributes(Orb orb, OrbBasedComponent orbBasedComponent) {
		orbBasedComponent.setOrbOriginal(orb);
		orbBasedComponent.setId(orb.getOrbInternalId());
		orbBasedComponent.setParentId(Long.parseLong(orb.getUserDefinedProperties().get(OrbBasedComponent.ATTR_PARENT)));
	}
	
	public abstract T transform(Orb orb);
}
