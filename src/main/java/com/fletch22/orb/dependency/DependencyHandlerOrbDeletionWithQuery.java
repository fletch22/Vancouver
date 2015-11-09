package com.fletch22.orb.dependency;

import com.fletch22.orb.Orb;
import com.fletch22.orb.query.CriteriaManager;

public class DependencyHandlerOrbDeletionWithQuery implements DependencyHandler {
	
	CriteriaManager queryManager;
	Orb orb;
	boolean isDeleteDependencies;
	
	public DependencyHandlerOrbDeletionWithQuery(Orb orb, boolean isDeleteDependencies) {
		this.orb = orb;
		this.isDeleteDependencies = isDeleteDependencies;
	}

	@Override
	public void check() {
		long orbInternalId = this.orb.getOrbInternalId();
		if (isDeleteDependencies) {
			queryManager.removeQueryFromCollection(orbInternalId);
		} else {
			boolean doesExist = queryManager.doesQueryExist(orbInternalId);
			if (doesExist) {
				String message = String.format("Encountered problem deleting orb '%s'. Orb has at least one dependency. A query exists that depends on the orb.", orbInternalId);
				throw new RuntimeException(message);
			}
		}
	}
	
	public void setQueryManager(CriteriaManager queryManager) {
		this.queryManager = queryManager;
	}
}
