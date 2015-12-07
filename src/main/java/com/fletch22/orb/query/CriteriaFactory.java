package com.fletch22.orb.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.local.Cache;

@Component
public class CriteriaFactory {
	
	@Autowired
	Cache cache;
	
	public Criteria createInstance(OrbType orbType, String label) {
		Criteria criteria = new CriteriaImpl(orbType, label);
		
		return criteria;
	}
}
