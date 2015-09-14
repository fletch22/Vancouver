package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.serialization.GsonSerializable;

@Component
public class CriteriaFactory {
	
	@Autowired
	Cache cache;
	
	public Criteria getInstance(OrbType orbType) {
		Criteria criteria = new Criteria(orbType);
		criteria.cache = cache;
		
		return criteria;
	}
	
	public static class Criteria implements GsonSerializable {
		
		transient Logger logger = LoggerFactory.getLogger(Criteria.class);
		
		OrbType orbType;
		
		transient Cache cache;
		
		public List<LogicalConstraint> logicalConstraintsList = new ArrayList<LogicalConstraint>();
		
		private Criteria(OrbType orbType) {
			this.orbType = orbType;
		}
		
		public Criteria add(LogicalConstraint logicalConstraint) {

			logicalConstraintsList.add(logicalConstraint);
			
			return this;
		}
		
		public Criteria add(Constraint constraint) {

			LogicalConstraint logicalConstraint = new LogicalConstraint(LogicalOperator.AND, constraint);
			logicalConstraintsList.add(logicalConstraint);
			
			return this;
		}
		
		public long getOrbTypeInternalId() {
			return orbType.id;
		}

//		@Override
		public StringBuilder toJson() {
			throw new NotImplementedException("Not yet implemented.");
		}
		
	}
}
