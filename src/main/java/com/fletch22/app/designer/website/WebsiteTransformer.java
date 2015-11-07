package com.fletch22.app.designer.website;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class WebsiteTransformer extends DomainTransformer<Website> {
	
	public Website transform(Orb orb) {
		
		Website website = new Website();
		
		this.setBaseAttributes(orb, website);
		website.label = orb.getUserDefinedProperties().get(Website.ATTR_LABEL);
		
		return website;
	}
}
