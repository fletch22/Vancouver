package com.fletch22.orb.limitation;

import java.util.List;

import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.CriteriaManager;

public interface LimitationManager extends CriteriaManager {
	
	public long addDefaultLimitation(Criteria criteria);
	
	public Criteria removeDefaultLimitation(long criteriaId);
	
	public List<Criteria> getOrbsDefaultLimitations(long orbTypeInternalId);
}
