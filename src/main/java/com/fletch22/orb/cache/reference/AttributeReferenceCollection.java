package com.fletch22.orb.cache.reference;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.AttributeArrows;

@Component
@Scope("prototype")
public class AttributeReferenceCollection {

public Map<Long, TargetLineup> targetLineups = new LinkedHashMap<Long, TargetLineup>();

	static Logger logger = LoggerFactory.getLogger(AttributeReferenceCollection.class);
	
	public void addReferences(long orbInternalId, String attributeName, List<DecomposedKey> keys) {
		for (DecomposedKey key: keys) {
			addReference(orbInternalId, attributeName, key.orbInternalId, key.attributeName);
		}
	}
	
	public void addReference(long orbInternalIdArrow, String nameOfArrowAttribute, long orbInternalIdTarget, String nameOfTargetAttribute) {
		TargetLineup targetLineup = targetLineups.get(orbInternalIdTarget);
		
		if (targetLineup == null) {
			targetLineup = new TargetLineup();
			targetLineups.put(orbInternalIdTarget, targetLineup);
		}
		
		Target target = targetLineup.targets.get(nameOfTargetAttribute);
		if (target == null) {
			target = new Target();
			targetLineup.targets.put(nameOfTargetAttribute, target);
		}
		
		ArrowCluster arrowCluster = target.arrowClusterCollection.get(orbInternalIdArrow);
		
		if (arrowCluster == null) {
			arrowCluster = new ArrowCluster();
			target.arrowClusterCollection.put(orbInternalIdArrow, arrowCluster);
		}
		
		if (!arrowCluster.arrows.contains(nameOfArrowAttribute)) {
			arrowCluster.arrows.add(nameOfArrowAttribute);
		}
	}
	
	protected int countArrows() {
		
		int count = 0;
		Set<Long> targetLineupSet = targetLineups.keySet();
		for (long orbInternalIdTarget: targetLineupSet) {
			TargetLineup targetLineup = targetLineups.get(orbInternalIdTarget);
			
			Set<String> targetKeySet = targetLineup.targets.keySet();
			for (String nameOfAttribute: targetKeySet) {
				Target target = targetLineup.targets.get(nameOfAttribute);
				
				Set<Long> arrowSet = target.arrowClusterCollection.keySet();
				for (Long arrowTarget: arrowSet)  {
					ArrowCluster arrowCluster = target.arrowClusterCollection.get(arrowTarget);
					count += arrowCluster.arrows.size();
				}
			}
		}
		
		return count;
	}
	
	protected int countArrowsPointingToTarget(long orbInternalIdTarget, String attributeName) {
		
		int count = 0;
		TargetLineup targetLineup = targetLineups.get(orbInternalIdTarget);
		if (targetLineup != null) {
			
			Target target = targetLineup.targets.get(attributeName);
			if (target != null) {
				Set<Long> arrowSet = target.arrowClusterCollection.keySet();
				for (Long arrowTarget: arrowSet)  {
					ArrowCluster arrowCluster = target.arrowClusterCollection.get(arrowTarget);
					count += arrowCluster.arrows.size();
				}
			}
		}
		
		return count;
	}
	
//	public Map<Long, ArrowCluster> getArrowsPointingAtTarget(long orbTargetInternalId, String attributeName) {
//		LinkedHashMap<Long, ArrowCluster> arrowClusterMap = new LinkedHashMap<Long, ArrowCluster>();
//		
//		TargetLineup targetLineup = targetLineups.get(orbTargetInternalId);
//		
//		if (targetLineup != null) {
//			Target target = targetLineup.targets.get(attributeName);
//			if (target != null) {
//				arrowClusterMap = target.arrowClusterCollection;
//			}
//		}
//		
//		return arrowClusterMap;
//	}
	
	public void removeArrows(long orbInternalIdArrow, String attributeNameArrow, List<DecomposedKey> keys) {
		for (DecomposedKey key: keys) {
			removeArrowsFromAttributeRefs(orbInternalIdArrow, attributeNameArrow, key);
		}
	}

	private void removeArrowsFromAttributeRefs(long orbInternalIdArrow, String attributeNameArrow, DecomposedKey decomposedKey) {
		TargetLineup targetLineup = targetLineups.get(decomposedKey.orbInternalId);
		
		removeArrowFromLineup(targetLineup, orbInternalIdArrow, attributeNameArrow, decomposedKey);
		
		if (targetLineup != null && targetLineup.targets.size() == 0) {
			targetLineups.remove(decomposedKey.orbInternalId);
		}
	}
	
	private void removeArrowFromLineup(TargetLineup targetLineup, long orbInternalIdArrow, String attributeNameArrow, DecomposedKey decomposedKey) {
		if (targetLineup != null) {
			Target target = targetLineup.targets.get(decomposedKey.attributeName);
			if (target != null) {
				removeArrowFromTarget(target, orbInternalIdArrow, attributeNameArrow);
				if (target.arrowClusterCollection.size() == 0) {
					targetLineup.targets.remove(decomposedKey.attributeName);
				}
			}
		}
	}

	private void removeArrowFromTarget(Target target, long orbInternalIdArrow, String attributeNameArrow) {
		ArrowCluster arrowCluster = target.arrowClusterCollection.get(orbInternalIdArrow);
		if (arrowCluster != null) {
			arrowCluster.arrows.remove(attributeNameArrow);
			if (arrowCluster.arrows.size() == 0) {
				target.arrowClusterCollection.remove(orbInternalIdArrow);
			}
		}
	}
	
	public void removeArrows(long orbInternalId, Map<String, List<DecomposedKey>> namesToValuesMap) {
		
		Set<String> attributeNameArrowSet = namesToValuesMap.keySet();
		for (String attributeNameArrow : attributeNameArrowSet) {
			List<DecomposedKey> list = namesToValuesMap.get(attributeNameArrow);
			for (DecomposedKey decomposedKey : list) {
				removeArrowsFromAttributeRefs(orbInternalId, attributeNameArrow, decomposedKey);
			}
		}
	}
	
	public void removeTarget(long orbTargetInternalId) {
		targetLineups.remove(orbTargetInternalId);
	}
	
	public void removeTarget(long orbTargetInternalId, String attributeNameTarget) {
		TargetLineup targetLineup = targetLineups.get(orbTargetInternalId);
		if (targetLineup != null) {
			targetLineup.targets.remove(attributeNameTarget);
		}
	}
	
	public void renameAttribute(long orbInternalId, String attributeNameOld, String attributeNameNew) {
		renameTargets(orbInternalId, attributeNameOld, attributeNameNew);
		renameArrows(orbInternalId, attributeNameOld, attributeNameNew);
	}

	private void renameArrows(long orbInternalId, String attributeNameOld, String attributeNameNew) {
		Set<Long> targetLineupSet = targetLineups.keySet();
		for (long orbInternalIdTarget: targetLineupSet) {
			TargetLineup targetLineup = targetLineups.get(orbInternalIdTarget);
			
			Set<String> targetKeySet = targetLineup.targets.keySet();
			for (String nameOfAttribute: targetKeySet) {
				Target target = targetLineup.targets.get(nameOfAttribute);
				
				if (target == null) {
					Target test = targetLineup.targets.get(attributeNameNew);
					logger.info("Test target is null? {}", test == null);
				}
				
				Set<Long> arrowSet = target.arrowClusterCollection.keySet();
				for (long arrowTarget: arrowSet)  {
					
					if (arrowTarget == orbInternalId) {
						ArrowCluster arrowCluster = target.arrowClusterCollection.get(arrowTarget);
						if (arrowCluster.arrows.contains(attributeNameOld)) {
							arrowCluster.arrows.remove(attributeNameOld);
							arrowCluster.arrows.add(attributeNameNew);
						}
					}
				}
			}
		}
	}

	private void renameTargets(long orbInternalIdTarget, String attributeNameTargetOld, String attributeNameTargetNew) {
		TargetLineup targetLineup = targetLineups.get(orbInternalIdTarget);
		
		if (targetLineup != null) {
			Target target = targetLineup.targets.remove(attributeNameTargetOld);
			if (target != null) {
				targetLineup.targets.put(attributeNameTargetNew, target);
			}
		}
	}

	public void clear() {
		targetLineups.clear();
	}
	
	public Map<Long, AttributeArrows> getArrowsPointingAtTarget(long orbInternalIdTarget) {
		Map<Long, AttributeArrows> attributeArrowMap = new HashMap<Long, AttributeArrows>();

		TargetLineup targetLineup = this.targetLineups.get(orbInternalIdTarget);
		
		if (targetLineup != null) {
			Set<String> targetKeys = targetLineup.targets.keySet();
			
			for (String attributeName: targetKeys) {
				Target target = targetLineup.targets.get(attributeName);
				if (target != null) {
					
					LinkedHashMap<Long, ArrowCluster> arrowClusterCollection = target.arrowClusterCollection;
					Set<Long> arrowSet = arrowClusterCollection.keySet();
					for (long arrow: arrowSet) {
						
						ArrowCluster arrowCluster = arrowClusterCollection.get(arrow);
						for (String arrowAttribute : arrowCluster.arrows) {
						
							AttributeArrows attributeArrows = attributeArrowMap.get(arrow);
							if (attributeArrows == null) {
								attributeArrows = new AttributeArrows();
								attributeArrowMap.put(arrow, attributeArrows);
							}
							attributeArrows.attributesContainingArrows.add(arrowAttribute);
						}
					}
				}
			}
		}
		
		return attributeArrowMap;
	}

}
