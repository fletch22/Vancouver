package com.fletch22.orb.cache.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.cache.reference.OrbReference;

@Component
public class CacheComponentsFactory {
	
	@Autowired
	OrbReference orbReference;
	
	public CacheComponentsDto getInstance() {
		CacheComponentsDto cacheComponentsDto = new CacheComponentsDto();
		cacheComponentsDto.orbTypeCollection = (OrbTypeCollection) getApplicationContext().getBean(OrbTypeCollection.class);
		cacheComponentsDto.orbCollection = (OrbCollection) getApplicationContext().getBean(OrbCollection.class); 
		cacheComponentsDto.orbCollection.orbReference = orbReference;
		cacheComponentsDto.queryCollection = (QueryCollection) getApplicationContext().getBean(QueryCollection.class);
		
		return cacheComponentsDto;
	}
	
	public ApplicationContext getApplicationContext() {
		return Fletch22ApplicationContext.getApplicationContext();
	}
	
	public CacheComponentsDto getInstance(Cache cache) {
		CacheComponentsDto dto = new CacheComponentsDto();
		
		dto.orbTypeCollection = cache.orbTypeCollection;
		dto.orbCollection = cache.orbCollection;
		dto.queryCollection = cache.queryCollection;
		
		return dto;
	}
}
