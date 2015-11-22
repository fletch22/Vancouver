package com.fletch22.orb.query;

import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface QueryManager extends CriteriaManager {
	
	public OrbResultSet executeQuery(long orbTypeInternalId, String queryLabel);

	public OrbResultSet executeQuery(Criteria criteria);
	
	public OrbResultSet findByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind);
}
