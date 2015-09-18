package com.fletch22.orb.cache.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.query.QueryCollection;

@Component
public class CacheComponentsFactory {
	
	@Autowired
	OrbReference orbReference;
	
	public CacheComponentsDto getInstance() {
		CacheComponentsDto cacheComponentsDto = new CacheComponentsDto();
		cacheComponentsDto.orbTypeCollection = new OrbTypeCollection();
		cacheComponentsDto.orbCollection = new OrbCollection();
		cacheComponentsDto.orbCollection.orbReference = orbReference;
		cacheComponentsDto.queryCollection = new QueryCollection();
		
		return cacheComponentsDto;
	}
	
	public CacheComponentsDto getInstance(Cache cache) {
		CacheComponentsDto dto = new CacheComponentsDto();
		
		dto.orbTypeCollection = cache.orbTypeCollection;
		dto.orbCollection = cache.orbCollection;
		dto.queryCollection = cache.queryCollection;
		
		return dto;
	}
}
