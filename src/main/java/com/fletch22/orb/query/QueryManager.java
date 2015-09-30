package com.fletch22.orb.query;

import java.util.List;

import com.fletch22.orb.Orb;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public interface QueryManager {
	public long create(Criteria criteria);
	
	public void create(Orb orb, Criteria criteria);
	
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies);
	
	public void addQueryToCollection(long orbInternalIdQuery, Criteria criteria);
	
	public void removeQueryFromCollection(long orbInternalIdQuery);

	public void nukeAllQueries();
	
	public boolean doesQueryExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
	
	public List<Orb> executeQuery(String queryLabel);
	
	public List<Orb> executeQuery(Criteria criteria);
	
	public void handleAttributeRename(long orbTypeInternalId, String attributeOldName, String attributeNewName);

	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteQueryIfAttributeFound);
	
	public Orb findDistinctByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind);
}
