package com.fletch22.orb.query;

import com.fletch22.orb.OrbType;

public class CriteriaImpl extends Criteria {
	
	public CriteriaImpl(OrbType orbType, String label) {
		this.orbType = orbType;
		this.label = label;
	}
}