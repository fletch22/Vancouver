package com.fletch22.orb.query;

import com.fletch22.orb.OrbType;

public class CriteriaAggregate extends Criteria {
	
	public String fieldOfInterest;
	
	public CriteriaAggregate(OrbType orbType, String label, String fieldOfInterest) {
		this.orbType = orbType;
		this.label = label;
		this.fieldOfInterest = fieldOfInterest;
	}
}