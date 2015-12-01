package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.serialization.GsonSerializable;

@Component
public class CriteriaFactory {
	
	@Autowired
	Cache cache;
	
	public Criteria createInstance(OrbType orbType, String label) {
		Criteria criteria = new Criteria(orbType, label);
		
		return criteria;
	}
	
	public static class Criteria implements GsonSerializable {
		
		transient Logger logger = LoggerFactory.getLogger(Criteria.class);
		
		transient public static final long UNSET_CRITERIA_ID = -1;
		
		private long criteriaId = UNSET_CRITERIA_ID;
		private OrbType orbType;
		private String label;
		private boolean hasIdBeenSet = false;
		private ArrayList<CriteriaSortInfo> sortInfoList = new ArrayList<CriteriaSortInfo>();
		public Criteria parent;
		
		public LogicalConstraint logicalConstraint = null;
		
		private Criteria(OrbType orbType, String label) {
			this.orbType = orbType;
			this.label = label;
		}
		
		public void setId(long id) {
			
			if (hasIdBeenSet) {
				throw new RuntimeException("Encountered a problem. Criteria Id may only be set once. The intent of this constraint is to avoid corruption in particular collections.");
			}
			
			this.criteriaId = id;
			
			hasIdBeenSet = true;
		}
		
		public long getCriteriaId() {
			return this.criteriaId;
		}
		
		public void setSortOrder(CriteriaSortInfo criteriaSortInfo) {
			this.sortInfoList = new ArrayList<CriteriaSortInfo>();
			this.sortInfoList.add(criteriaSortInfo);
		}
		
		public Criteria addAnd(Constraint ... constraintArray) {

			for (Constraint constraint : constraintArray) {
				add(LogicalOperator.AND, constraint);
			}
			
			return this;
		}
		
		public Criteria addOr(Constraint ... constraintArray) {

			for (Constraint constraint : constraintArray) {
				add(LogicalOperator.OR, constraint);
			}
			
			return this;
		}
		
		private Criteria add(LogicalOperator logicalOperator, Constraint constraint) {

			if (this.logicalConstraint == null) {
				this.logicalConstraint = new LogicalConstraint(logicalOperator, constraint);
			} else {
				if (!logicalOperator.equals(this.logicalConstraint.logicalOperator)) {
					throw new RuntimeException("Encountered problem adding constraint with logical operator that is different than grouping''s common logical operator.");
				}
				this.logicalConstraint.constraintList.add(constraint);
			}
			
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

		public Criteria getParent() {
			return parent;
		}

		public void setParent(Criteria parent) {
			this.parent = parent;
		}

		public boolean isParent() {
			return (this.parent != null);
		}
	}
}
