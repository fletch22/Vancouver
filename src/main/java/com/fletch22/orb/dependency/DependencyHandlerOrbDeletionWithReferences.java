package com.fletch22.orb.dependency;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.cache.local.Cache;

public class DependencyHandlerOrbDeletionWithReferences implements DependencyHandler {
	
	private OrbManager orbManager;
	private Cache cache;

	Orb orb;
	boolean isDeleteDependencies;
	
	public DependencyHandlerOrbDeletionWithReferences(Orb orb, boolean isDeleteDependencies) {
		this.orb = orb;
		this.isDeleteDependencies = isDeleteDependencies;
	}

	@Override
	public void check() {
		if (isDeleteDependencies) {
			orbManager.resetAllReferencesPointingToOrb(orb);
		} else {
			long orbInternalId = orb.getOrbInternalId();
			boolean doesExist = this.cache.orbCollection.doesReferenceToOrbExist(orb);
			if (doesExist) {
				String message = String.format("Encountered problem deleting orb '%s'. Orb has at least one dependency. Specify that dependencies should be deleted automatically by passing 'true' for 'isDeleteDependencies'.", orbInternalId);
				throw new RuntimeException(message);
			}
		}
	}
	
	public void setOrbManager(OrbManager orbManager) {
		this.orbManager = orbManager;
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
