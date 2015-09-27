package com.fletch22.orb.query;

import com.fletch22.orb.Orb;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface QueryManager {
	public long create(Criteria criteria);
	
	public void create(Orb orb, Criteria criteria);
	
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies);
	
	public void addQueryToCollection(long orbInternalIdQuery, Criteria criteria);
	
	public void removeQueryFromCollection(long orbInternalIdQuery);

	public void deleteAllQueries();
	
	public boolean doesQueryExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
	
	public ResultSet executeQuery(String queryLabel);
	
	public void handleAttributeRename(long orbTypeInternalId, String attributeOldName, String attributeNewName);

	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteQueryIfAttributeFound);
}
