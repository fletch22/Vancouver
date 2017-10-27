package com.fletch22.app.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;

public abstract class DomainTransformer<T extends OrbBasedComponent> {
	
	private static final Logger logger = LoggerFactory.getLogger(DomainTransformer.class);
	
	protected void setBaseAttributes(Orb orb, OrbBasedComponent orbBasedComponent) {
		orbBasedComponent.setOrbOriginal(orb);
		orbBasedComponent.setId(orb.getOrbInternalId());
		orbBasedComponent.setParentId(Long.parseLong(orb.getUserDefinedProperties().get(OrbBasedComponent.ATTR_PARENT)));
		
		String ordinal = orb.getUserDefinedProperties().get(Child.ATTR_ORDINAL);
		orbBasedComponent.setOrdinal((ordinal == null) ? Child.UNSET_ORDINAL : ordinal);
	}
	
	public abstract T transform(Orb orb);
}
