package com.fletch22.orb.cache.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.cache.query.QueryCollection;

@Component
public class CacheComponentsFactory {
	
	Logger logger = LoggerFactory.getLogger(CacheComponentsFactory.class);
	
	public CacheComponentsDto getInstance() {
		
		logger.debug("Creating copy of cache.");
		
		CacheComponentsDto cacheComponentsDto = new CacheComponentsDto();
		cacheComponentsDto.orbTypeCollection = (OrbTypeCollection) getApplicationContext().getBean(OrbTypeCollection.class);
		cacheComponentsDto.orbCollection = (OrbCollection) getApplicationContext().getBean(OrbCollection.class); 
		cacheComponentsDto.queryCollection = (CriteriaCollection) getApplicationContext().getBean(QueryCollection.class);
		
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
