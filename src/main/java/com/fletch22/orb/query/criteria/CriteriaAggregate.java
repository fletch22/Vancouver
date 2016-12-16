package com.fletch22.orb.query.criteria;

import org.apache.commons.lang3.StringUtils;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;


public class CriteriaAggregate extends Criteria {
	
	public String[] fieldsOfInterest;
	
	public CriteriaAggregate(long orbTypeInternalId, String label, String[] fieldOfInterest) {
		this.orbTypeInternalId = orbTypeInternalId;
		this.label = label;
		this.fieldsOfInterest = fieldOfInterest;
	}

	public StringBuffer getDescription() {
		StringBuffer description = new StringBuffer();
		
		description.append(StringUtils.SPACE);
		
		OrbType orbType = getOrbTypeManager().getOrbType(orbTypeInternalId);
		
		description.append(String.format("Orb Type [%s]'s attribute(s) [%s] ", orbType.label, String.join(",", this.fieldsOfInterest)));
		
		return description;
	}
	
	public OrbTypeManager getOrbTypeManager() {
		return (OrbTypeManager) Fletch22ApplicationContext.getApplicationContext().getBean(OrbTypeManager.class);
	}
}