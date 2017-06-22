package com.fletch22.app.designer.dataUniverse;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class DataUniverseTransformer extends DomainTransformer<DataUniverse> {
	
	public DataUniverse transform(Orb orb) {
		
		DataUniverse page = new DataUniverse();
		
		this.setBaseAttributes(orb, page);
		page.label = orb.getUserDefinedProperties().get(DataUniverse.ATTR_LABEL);
		
		return page;
	}
}
