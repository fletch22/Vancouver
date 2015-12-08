package com.fletch22.orb.query;

import com.fletch22.orb.OrbType;

public class CriteriaStandard extends Criteria {
	
	public CriteriaStandard(OrbType orbType, String label) {
		this.orbType = orbType;
		this.label = label;
	}
}