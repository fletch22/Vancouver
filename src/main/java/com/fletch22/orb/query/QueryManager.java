package com.fletch22.orb.query;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaManager;

public interface QueryManager extends CriteriaManager {
	
	public OrbResultSet executeQuery(String queryLabel);

	public OrbResultSet executeQuery(Criteria criteria);
	
	public OrbResultSet findByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind);
	
	public OrbResultSet findAll(long orbTypeInternalId);
	
	public CriteriaCollection getCriteriaCollection();
}
