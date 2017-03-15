package com.fletch22.orb.query.constraint;

import java.util.ArrayList;
import java.util.List;

import com.fletch22.orb.Orb;

public class ConstraintShaper {

	private long orbTypeInternalId;
	private List<Orb> aggregateResultExclusionsCollection = new ArrayList<>();

	public ConstraintShaper(long orbTypeInternalId) {
		this.orbTypeInternalId = orbTypeInternalId;
	}
	
	public ConstraintShaper(long orbTypeInternalId, Orb aggregateResultExclusion) {
		this.orbTypeInternalId = orbTypeInternalId;
		
		if (aggregateResultExclusion == null) {
			throw new RuntimeException("Encountered problem constructing object. Argument cannot be null.");
		}
		
		this.aggregateResultExclusionsCollection.add(aggregateResultExclusion);
	}

	public long getOrbTypeInternalId() {
		return this.orbTypeInternalId;
	}
	
	public List<Orb> getAggregateResultExclusionCollection() {
		return this.aggregateResultExclusionsCollection;
	}
}
