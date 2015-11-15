package com.fletch22.orb.limitation;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.OrbResultSet;

@Component
public class LimitationManagerImpl implements LimitationManager {

	@Override
	public void handleAttributeRenameEvent(long orbTypeInternalId, String oldAttributeName, String newAttributeName) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void handleAttributeDeleteEvent(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public long create(Criteria criteria) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void removeFromCollection(long key) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public Criteria get(long orbInternalIdQuery) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void handleTypeDeleteEvent(long orbTypeInternalId, boolean isDeleteDependencies) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void handleInstanceDeleteEvent(long orbTypeInternalId, boolean isDeleteDependencies) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void addToCollection(Criteria criteria) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void nukeAllCriteria() {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public boolean doesQueryExist(long orbInternalIdQuery) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public OrbResultSet executeQuery(long orbTypeInternalId, String queryLabel) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public OrbResultSet executeQuery(Criteria criteria) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public OrbResultSet findByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind) {
		throw new NotImplementedException("Not yet implemented.");
	}
}
