package com.fletch22.orb.query;

import com.fletch22.orb.attribute.OrbEventAware;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface CriteriaManager extends OrbEventAware {

	public long addToCollection(Criteria criteria);
	
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies);
	
	public void attach(Criteria criteria);
	
	public void detach(long criteriaId);
	
	public void nukeAllCriteria();
	
	public boolean doesCriteriaExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
}
