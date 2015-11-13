package com.fletch22.orb.limitation;

import com.fletch22.orb.Orb;
import com.fletch22.orb.attribute.OrbTypeEventAware;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface LimitationManager extends OrbTypeEventAware {
	
	public long create(Criteria criteria);
	
	public void create(Orb orb, Criteria criteria);
	
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies);
	
	public void addToCollection(long criteriaOrbInternalId, Criteria criteria);
	
	public void removeFromCollection(long key);

	public void nukeAllDataLimitations();
	
	public boolean doesDataLimitationExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
	
	public boolean executeDataLimitationTest(long orbTypeInternalId, String queryLabel);
	
	public boolean executeDataLimitationTest(Criteria criteria);
}
