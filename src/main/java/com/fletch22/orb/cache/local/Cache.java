package com.fletch22.orb.cache.local;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;

@Component
public class Cache {

	public OrbCollection orbCollection;
	public OrbTypeCollection orbTypeCollection;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	CacheComponentsFactory cacheComponentsFactory;
	
	@PostConstruct
	public void postConstruct() {
		CacheComponentsDto dto = cacheComponentsFactory.getInstance();
		orbTypeCollection = dto.orbTypeCollection;
		orbCollection = dto.orbCollection;
	}
	
	public CacheComponentsDto getCacheComponentsDto() {
		return cacheComponentsFactory.getInstance(this);
	}

	public void clearAllItemsFromCache() {
		 orbManager.deleteAllOrbs();
		 orbTypeManager.deleteAllTypes();
	}
}
