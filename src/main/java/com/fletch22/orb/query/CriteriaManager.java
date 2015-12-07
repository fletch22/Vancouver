package com.fletch22.orb.query;

import java.util.List;

import com.fletch22.orb.attribute.OrbEventAware;
import com.fletch22.orb.query.CriteriaImpl;

public interface CriteriaManager extends OrbEventAware {

	public long addToCollection(CriteriaImpl criteria);
	
	public void delete(long criteriaId, boolean isDeleteDependencies);
	
	public void attach(CriteriaImpl criteria);
	
	public void detach(long criteriaId);
	
	public List<CriteriaImpl> getOrbsTypeCriteria(long orbTypeInternalId);
	
	public void nukeAllCriteria();
	
	public boolean doesCriteriaExist(long orbInternalIdQuery);
	
	public CriteriaImpl get(long orbInternalIdQuery);
}
