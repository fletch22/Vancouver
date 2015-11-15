package com.fletch22.orb.query;

import com.fletch22.orb.attribute.OrbEventAware;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface CriteriaManager extends OrbEventAware {

	public long create(Criteria criteria);
	
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies);
	
	public void addToCollection(Criteria criteria);
	
	public void removeFromCollection(long orbInternalIdQuery);

	public void nukeAllCriteria();
	
	public boolean doesQueryExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
	
	public OrbResultSet executeQuery(long orbTypeInternalId, String queryLabel);
	
	public OrbResultSet executeQuery(Criteria criteria);
	
	public OrbResultSet findByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind);
}
