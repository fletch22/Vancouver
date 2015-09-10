package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.local.CacheEntry;

@Component
public class CriteriaFactory {
	
	@Autowired
	Cache cache;
	
	public Criteria getInstance(long orbTypeInternalId) {
		Criteria criteria = new Criteria(orbTypeInternalId);
		criteria.cache = cache;
		
		return criteria;
	}
	
	public static class Criteria {
		
		Logger logger = LoggerFactory.getLogger(Criteria.class);
		
		long orbTypeInternalId;
		Cache cache;
		public List<LogicalConstraint> logicalConstraintsList = new ArrayList<LogicalConstraint>();
		
		private Criteria(long orbTypeInternalId) {
			this.orbTypeInternalId = orbTypeInternalId;
		}
		
		public Criteria add(LogicalConstraint logicalConstraint) {

			logicalConstraintsList.add(logicalConstraint);
			
			return this;
		}
		
		public long getOrbTypeInternalId() {
			return this.orbTypeInternalId;
		}
		
	}
}
