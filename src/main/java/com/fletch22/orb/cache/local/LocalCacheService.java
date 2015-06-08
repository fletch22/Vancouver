package com.fletch22.orb.cache.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;

@Component
public class LocalCacheService implements CacheService {

	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	public void clearAllItemsFromCache() {
		 orbManager.deleteAllOrbInstances();
		 orbTypeManager.deleteAllTypes();
	}
	
}
