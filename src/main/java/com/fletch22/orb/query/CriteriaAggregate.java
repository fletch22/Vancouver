package com.fletch22.orb.query;


public class CriteriaAggregate extends Criteria {
	
	public String fieldOfInterest;
	
	public CriteriaAggregate(long orbTypeInternalId, String label, String fieldOfInterest) {
		this.orbTypeInternalId = orbTypeInternalId;
		this.label = label;
		this.fieldOfInterest = fieldOfInterest;
	}
}