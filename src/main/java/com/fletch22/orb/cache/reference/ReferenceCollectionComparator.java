package com.fletch22.orb.cache.reference;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.CacheDifferenceReasons;
import com.fletch22.orb.cache.local.ComparisonResult;

@Component
public class ReferenceCollectionComparator {

	public ComparisonResult areSame(ReferenceCollection referenceCollection1, ReferenceCollection referenceCollection2) {
		return areSameAttributeCollections(referenceCollection1.attributeReferenceCollection, referenceCollection2.attributeReferenceCollection);
	}
	
	private ComparisonResult areSameAttributeCollections(AttributeReferenceCollection attributeReferenceCollection1, AttributeReferenceCollection attributeReferenceCollection2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		// Check for same size
		Map<Long, TargetLineup> targetLineupMap1 = attributeReferenceCollection1.targetLineups;
		Map<Long, TargetLineup> targetLineupMap2 = attributeReferenceCollection2.targetLineups;
		
		if (targetLineupMap1.size() != targetLineupMap2.size()) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_ATTRIBUTE_REFERENCE_REPOSITORY_DIFFERS;
		} else {
			Set<Long> targetLineupsKeys = targetLineupMap1.keySet();
			for (long orbTypeInternalIdTarget: targetLineupsKeys) {
				
				comparisonResult = compareTargetLineups(targetLineupMap1, targetLineupMap2, orbTypeInternalIdTarget);
				if (!comparisonResult.isSame) {
					return comparisonResult;
				}
			}
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareTargetLineups(Map<Long, TargetLineup> targetLineupMap1, Map<Long, TargetLineup> targetLineupMap2, long orbTypeInternalIdTarget) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		TargetLineup targetLineup1 = targetLineupMap1.get(orbTypeInternalIdTarget);
		if (!targetLineupMap2.containsKey(orbTypeInternalIdTarget)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_REFERENCE_TARGET_LINEUPS_DIFFER;
			return comparisonResult;
		}
		
		TargetLineup targetLineup2 = targetLineupMap2.get(orbTypeInternalIdTarget);
		
		return compareTargetAttributes(targetLineup1, targetLineup2);
	}

	private ComparisonResult compareTargetAttributes(TargetLineup targetLineup1, TargetLineup targetLineup2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		Set<String> targetAttributeSet = targetLineup1.targets.keySet();
		for (String  targetAttributeName: targetAttributeSet) {
			
			if (!targetLineup2.targets.containsKey(targetAttributeName)) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_REFERENCE_TARGET_ATTRIBUTES_DIFFER;
				return comparisonResult;
			}
			
			comparisonResult = compareTargets(targetLineup1, targetLineup2, targetAttributeName);
			if (!comparisonResult.isSame) {
				return comparisonResult;
			}
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareTargets(TargetLineup targetLineup1, TargetLineup targetLineup2, String targetAttributeName) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		Target target1 = targetLineup1.targets.get(targetAttributeName);
		Target target2 = targetLineup2.targets.get(targetAttributeName);
		
		LinkedHashMap<Long, ArrowCluster> arrowClusterCollection1 = target1.arrowClusterCollection;
		LinkedHashMap<Long, ArrowCluster> arrowClusterCollection2 = target2.arrowClusterCollection;
		
		Set<Long> arrowKeys = arrowClusterCollection1.keySet();
		for (long orbInternalIdArrow: arrowKeys) {
			if (!arrowClusterCollection2.containsKey(orbInternalIdArrow)) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_REFERENCE_ARROW_CLUSTER_NOT_IDENTICAL;
				return comparisonResult;
			}
			
			comparisonResult = compareArrowClusters(arrowClusterCollection1, arrowClusterCollection2, orbInternalIdArrow);
			if (!comparisonResult.isSame) {
				return comparisonResult;
			}
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareArrowClusters(LinkedHashMap<Long, ArrowCluster> arrowClusterCollection1, LinkedHashMap<Long, ArrowCluster> arrowClusterCollection2, long orbInternalIdArrow) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		List<String> arrowCluster1 = arrowClusterCollection1.get(orbInternalIdArrow).arrows;
		List<String> arrowCluster2 = arrowClusterCollection2.get(orbInternalIdArrow).arrows;
		for (String attributeName1 : arrowCluster1) {
			if (!arrowCluster2.contains(attributeName1)) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_REFERENCE_ARROW_ATTRIBUTES_NOT_IDENTICAL;
				return comparisonResult;
			}
		}
		
		return comparisonResult;
	}
}
