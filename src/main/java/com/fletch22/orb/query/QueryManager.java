package com.fletch22.orb.query;

import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.Criteria;

public interface QueryManager extends CriteriaManager {
	
	public OrbResultSet executeQuery(String queryLabel);

	public OrbResultSet executeQuery(Criteria criteria);
	
	public OrbResultSet findByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind);
	
	public CriteriaCollection getCriteriaCollection();
}
