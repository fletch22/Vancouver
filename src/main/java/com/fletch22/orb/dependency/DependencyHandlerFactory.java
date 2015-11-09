package com.fletch22.orb.dependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.query.CriteriaManager;

@Component
public class DependencyHandlerFactory {
	
	@Autowired
	Cache cache;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	CriteriaManager queryManager;

	public DependencyHandler getOrbDeletionForQueryInstance(Orb orb, boolean isDeleteDependencies) {
		DependencyHandlerOrbDeletionWithQuery dep = new DependencyHandlerOrbDeletionWithQuery(orb, isDeleteDependencies);
		
		dep.setQueryManager(queryManager);
		
		return dep;
	}
	
	public DependencyHandler getOrbDeltionForReferencesInstance(Orb orb, boolean isDeleteDependencies) {
		DependencyHandlerOrbDeletionWithReferences dep = new DependencyHandlerOrbDeletionWithReferences(orb, isDeleteDependencies);
		
		dep.setCache(cache);
		dep.setOrbManager(orbManager);
		
		return dep;
	}
}
