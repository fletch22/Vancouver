package com.fletch22.orb.cache.local;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbCloner;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeCloner;
import com.fletch22.orb.cache.local.OrbCollection.OrbSteamerTrunk;

@Component
public class CacheCloner {
	
	Logger logger = LoggerFactory.getLogger(CacheCloner.class);

	@Autowired
	CacheComponentsFactory cacheComponentsFactory;
	
	@Autowired
	OrbCloner orbCloner;
	
	@Autowired
	OrbTypeCloner orbTypeCloner;
	
	public CacheComponentsDto clone(CacheComponentsDto cacheComponentsDto) {
		CacheComponentsDto clone = cacheComponentsFactory.getInstance();
		
		OrbTypeCollection orbTypeCollectionCloned = clone.orbTypeCollection;
		
		Map<Long, OrbType> orbTypeMap = cacheComponentsDto.orbTypeCollection.getQuickLookup();
		
		logger.info("Orb Type Count: {}", orbTypeMap.size());
		
		Set<Long> orbKeySet = orbTypeMap.keySet();
		for (long orbTypeInternalId : orbKeySet) {
			OrbType orbType = orbTypeMap.get(orbTypeInternalId);
			OrbType orbTypeCloned = orbTypeCloner.cloneOrb(orbType);
			orbTypeCollectionCloned.add(orbTypeCloned);
		}
		
		OrbCollection orbCollectionCloned = clone.orbCollection;
		
		Map<Long, OrbSteamerTrunk> quickLookup = cacheComponentsDto.orbCollection.getQuickLookup();
		orbKeySet = quickLookup.keySet();
		for (long orbInternalId : orbKeySet) {
			Orb orb = quickLookup.get(orbInternalId).orb;
			
			Orb orbCloned = orbCloner.cloneOrb(orb);
			OrbType orbType = orbTypeMap.get(orbCloned.getOrbTypeInternalId());
			
			logger.info("OrbType: {}", orbType.id);
			
			orbCollectionCloned.add(orbType, orbCloned);
		}
		
		return clone;
	}
	
}
