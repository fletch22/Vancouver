package com.fletch22.orb.cache.local;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.ReferenceCollection.ArrowCluster;
import com.fletch22.orb.cache.local.ReferenceCollection.Target;
import com.fletch22.orb.cache.local.ReferenceCollection.TargetLineup;

@Component
public class ReferenceCollectionComparator {

	public ComparisonResult areSame(ReferenceCollection referenceCollection1, ReferenceCollection referenceCollection2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		// Check for same size
		Map<Long, TargetLineup> targetLineupMap1 = referenceCollection1.targetLineups;
		Map<Long, TargetLineup> targetLineupMap2 = referenceCollection2.targetLineups;
		
		if (targetLineupMap1.size() != targetLineupMap2.size()) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_REFERENCE_REPOSITORY_DIFFERS;
		} else {
			Set<Long> targetLineupsKeys = targetLineupMap1.keySet();
			for (long orbTypeInternalIdTarget: targetLineupsKeys) {
				
				comparisonResult = compareTargetLineups(referenceCollection1, referenceCollection2, orbTypeInternalIdTarget);
				if (!comparisonResult.isSame) {
					return comparisonResult;
				}
			}
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareTargetLineups(ReferenceCollection referenceCollection1, ReferenceCollection referenceCollection2, long orbTypeInternalIdTarget) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		TargetLineup targetLineup1 = referenceCollection1.targetLineups.get(orbTypeInternalIdTarget);
		if (!referenceCollection2.targetLineups.containsKey(orbTypeInternalIdTarget)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_REFERENCE_TARGET_LINEUPS_DIFFER;
			return comparisonResult;
		}
		
		TargetLineup targetLineup2 = referenceCollection2.targetLineups.get(orbTypeInternalIdTarget);
		
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
