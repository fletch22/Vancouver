package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.serialization.GsonSerializable;

@Component
public class CriteriaFactory {
	
	@Autowired
	Cache cache;
	
	public Criteria getInstance(OrbType orbType, String label) {
		Criteria criteria = new Criteria(orbType, label);
		criteria.cache = cache;
		
		return criteria;
	}
	
	public static class Criteria implements GsonSerializable {
		
		transient Logger logger = LoggerFactory.getLogger(Criteria.class);
		
		private OrbType orbType;
		private String label;
		private ArrayList<CriteriaSortInfo> sortInfoList = new ArrayList<CriteriaSortInfo>();
		
		transient Cache cache;
		
		public List<LogicalConstraint> logicalConstraintList = new ArrayList<LogicalConstraint>();
		
		private Criteria(OrbType orbType, String label) {
			this.orbType = orbType;
			this.label = label;
		}
		
		public void setSortOrder(CriteriaSortInfo criteriaSortInfo) {
			this.sortInfoList = new ArrayList<CriteriaSortInfo>();
			this.sortInfoList.add(criteriaSortInfo);
		}
		
		public Criteria add(LogicalConstraint logicalConstraint) {

			logicalConstraintList.add(logicalConstraint);
			
			return this;
		}
		
		public Criteria add(Constraint constraint) {

			LogicalConstraint logicalConstraint = new LogicalConstraint(LogicalOperator.AND, constraint);
			logicalConstraintList.add(logicalConstraint);
			
			return this;
		}
		
		public long getOrbTypeInternalId() {
			return getOrbType().id;
		}

		public String getLabel() {
			return label;
		}

		public OrbType getOrbType() {
			return orbType;
		}

		public List<CriteriaSortInfo> getSortInfoList() {
			return this.sortInfoList;
		}
		
		public boolean hasSortCriteria() {
			return this.sortInfoList.size() > 0;
		}
	}
}
