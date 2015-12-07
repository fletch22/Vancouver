package com.fletch22.orb.query;

import java.util.List;

import com.fletch22.orb.attribute.OrbEventAware;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface CriteriaManager extends OrbEventAware {

	public long addToCollection(Criteria criteria);
	
	public void delete(long criteriaId, boolean isDeleteDependencies);
	
	public void attach(Criteria criteria);
	
	public void detach(long criteriaId);
	
	public List<Criteria> getOrbsTypeCriteria(long orbTypeInternalId);
	
	public void nukeAllCriteria();
	
	public boolean doesCriteriaExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
}
