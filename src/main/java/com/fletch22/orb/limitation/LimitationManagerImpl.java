package com.fletch22.orb.limitation;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

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
	public void create(Orb orb, Criteria criteria) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void addToCollection(long criteriaOrbInternalId, Criteria criteria) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void removeFromCollection(long key) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public void nukeAllDataLimitations() {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public boolean doesDataLimitationExist(long orbInternalIdQuery) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public Criteria get(long orbInternalIdQuery) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public boolean executeDataLimitationTest(long orbTypeInternalId, String queryLabel) {
		throw new NotImplementedException("Not yet implemented.");
	}

	@Override
	public boolean executeDataLimitationTest(Criteria criteria) {
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
}
