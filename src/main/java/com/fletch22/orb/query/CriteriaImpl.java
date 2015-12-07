package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.sort.CriteriaSortInfo;

public class CriteriaImpl extends Criteria {
	
	public CriteriaImpl(OrbType orbType, String label) {
		this.orbType = orbType;
		this.label = label;
	}
}