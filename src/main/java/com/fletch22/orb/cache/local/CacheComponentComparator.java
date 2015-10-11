package com.fletch22.orb.cache.local;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbComparer;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeComparator;
import com.fletch22.orb.cache.local.OrbCollection.OrbSteamerTrunk;
import com.fletch22.orb.cache.reference.ReferenceCollection;
import com.fletch22.orb.cache.reference.ReferenceCollectionComparator;

@Component
public class CacheComponentComparator {
	
	@Autowired
	OrbTypeComparator orbTypeComparator;
	
	@Autowired
	OrbComparer orbComparator;
	
	@Autowired
	ReferenceCollectionComparator referenceComparatorCollection;
	
	public ComparisonResult areSame(CacheComponentsDto cacheComponentsDto1, CacheComponentsDto cacheComponentsDto2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		
		OrbTypeCollection orbTypeCollection1 = cacheComponentsDto1.orbTypeCollection;
		OrbTypeCollection orbTypeCollection2 = cacheComponentsDto2.orbTypeCollection;
		
		if (orbTypeCollection1.getCount() != orbTypeCollection2.getCount()) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.NUMBER_ORB_TYPES_DIFFERENT;
		} else {
			OrbCollection orbCollection1 = cacheComponentsDto1.orbCollection;
			OrbCollection orbCollection2 = cacheComponentsDto2.orbCollection;
			
			if (orbCollection1.getCount() != orbCollection2.getCount()) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.NUMBER_ORBS_DIFFERENT;
			} else {
				comparisonResult = compareCollectionContents(cacheComponentsDto1, cacheComponentsDto2);			
			}
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareCollectionContents(CacheComponentsDto cacheComponentsDto1, CacheComponentsDto cacheComponentsDto2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		OrbTypeCollection orbTypeCollection1 = cacheComponentsDto1.orbTypeCollection;
		OrbTypeCollection orbTypeCollection2 = cacheComponentsDto2.orbTypeCollection;
		
		comparisonResult = compareOrbTypeCollection(orbTypeCollection1, orbTypeCollection2);
		
		if (comparisonResult.isSame) {
			OrbCollection orbCollection1 = cacheComponentsDto1.orbCollection;
			OrbCollection orbCollection2 = cacheComponentsDto2.orbCollection;
			
			comparisonResult = compareOrbCollection(orbCollection1, orbCollection2);
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareOrbTypeCollection(OrbTypeCollection orbTypeCollection1, OrbTypeCollection orbTypeCollection2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		Map<Long, OrbType> orbTypeMap1 = orbTypeCollection1.getQuickLookup();
		Map<Long, OrbType> orbTypeMap2 = orbTypeCollection2.getQuickLookup();
		Set<Long> orbTypeIdSet1 = orbTypeMap1.keySet();
		for (Long orbTypeInternalId : orbTypeIdSet1) {
			OrbType orbType1 = orbTypeMap1.get(orbTypeInternalId);
			OrbType orbType2 = orbTypeMap2.get(orbTypeInternalId);
			
			if (orbType2 == null) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TYPE_IN_CORRESPONDING_COLLECTION_IS_NULL;
			} else {
				comparisonResult = orbTypeComparator.areSame(orbType1, orbType2);
			}
			
		}
		
		return comparisonResult;
	}
	
	private ComparisonResult compareOrbCollection(OrbCollection orbCollection1, OrbCollection orbCollection2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		Map<Long, OrbSteamerTrunk> orbMap1 = orbCollection1.getQuickLookup();
		Map<Long, OrbSteamerTrunk> orbMap2 = orbCollection2.getQuickLookup();
		Set<Long> orbIdSet1 = orbMap1.keySet();
		for (Long orbInternalId : orbIdSet1) {
			Orb orbType1 = orbMap1.get(orbInternalId).orb;
			Orb orbType2 = orbMap2.get(orbInternalId).orb;
			
			if (orbType2 == null) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_IN_CORRESPONDING_COLLECTION_IS_NULL;
			} else {
				comparisonResult = orbComparator.areSame(orbType1, orbType2);
			}
		}
		
		if (comparisonResult.isSame) {
			ReferenceCollection referenceCollection1 = orbCollection1.orbReference.referenceCollection;
			ReferenceCollection referenceCollection2 = orbCollection2.orbReference.referenceCollection;
			
			comparisonResult = referenceComparatorCollection.areSame(referenceCollection1, referenceCollection2);
		}
		
		return comparisonResult;
	}
}