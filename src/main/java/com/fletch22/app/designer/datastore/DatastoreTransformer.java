package com.fletch22.app.designer.datastore;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class DatastoreTransformer extends DomainTransformer<Datastore> {
	
	public Datastore transform(Orb orb) {
		
		Datastore page = new Datastore();
		
		this.setBaseAttributes(orb, page);
		page.label = orb.getUserDefinedProperties().get(Datastore.ATTR_LABEL);
		
		return page;
	}
}
