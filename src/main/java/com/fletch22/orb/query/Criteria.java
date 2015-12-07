package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.serialization.GsonSerializable;

public abstract class Criteria implements GsonSerializable {

	transient Logger logger = LoggerFactory.getLogger(Criteria.class);
	
	transient public static final long UNSET_ID = -1;
	
	protected long criteriaId = UNSET_ID;
	protected OrbType orbType;
	protected String label;
	protected boolean hasIdBeenSet = false;
	protected ArrayList<CriteriaSortInfo> sortInfoList = new ArrayList<CriteriaSortInfo>();
	public long criteriaIdParent = UNSET_ID;
	
	public LogicalConstraint logicalConstraint = null;
	
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
	
	protected Criteria add(LogicalOperator logicalOperator, Constraint constraint) {

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

	public long getParentId() {
		return criteriaIdParent;
	}

	public void setParentId(long  parentId) {
		this.criteriaIdParent = parentId;
	}

	public boolean isParent() {
		return (this.criteriaIdParent != UNSET_ID);
	}
	
	public boolean hasConstraints() {
		return this.logicalConstraint != null && this.logicalConstraint.constraintList.size() > 0;
	}
}
