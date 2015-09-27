package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.OrbReference.DecomposedKey;

@Component
@Scope("prototype")
public class ReferenceCollection {
	
	Logger logger = LoggerFactory.getLogger(ReferenceCollection.class);

	public static final String REFERENCE_KEY_PREFIX = "^^^";
	public static final String ID_ATTRIBUTE_NAME_SEPARATOR = "^";
	
	Map<Long, TargetLineup> targetLineups = new LinkedHashMap<Long, TargetLineup>();
	
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
	
	public int countArrows() {
		
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
	
	public int countArrowsPointingToTarget(long orbInternalIdTarget, String attributeName) {
		
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
	
	public Map<Long, ArrowCluster> getArrowsPointingAtTarget(long orbTargetInternalId, String attributeName) {
		LinkedHashMap<Long, ArrowCluster> arrowClusterMap = new LinkedHashMap<Long, ArrowCluster>();
		
		TargetLineup targetLineup = targetLineups.get(orbTargetInternalId);
		
		if (targetLineup != null) {
			Target target = targetLineup.targets.get(attributeName);
			if (target != null) {
				arrowClusterMap = target.arrowClusterCollection;
			}
		}
		
		return arrowClusterMap;
	}
	
	public void removeArrows(long orbInternalIdArrow, String attributeNameArrow, List<DecomposedKey> keys) {
		for (DecomposedKey key: keys) {
			removeArrows(orbInternalIdArrow, attributeNameArrow, key);
		}
	}

	public void removeArrows(long orbInternalIdArrow, String attributeNameArrow, DecomposedKey decomposedKey) {
		TargetLineup targetLineup = targetLineups.get(decomposedKey.orbInternalId);
		
		removeArrowFromLineup(targetLineup, orbInternalIdArrow, attributeNameArrow, decomposedKey);
	}
	
	private void removeArrowFromLineup(TargetLineup targetLineup, long orbInternalIdArrow, String attributeNameArrow, DecomposedKey decomposedKey) {
		if (targetLineup != null) {
			Target target = targetLineup.targets.get(decomposedKey.attributeName);
			if (target != null) {
				removeArrowFromTarget(target, orbInternalIdArrow, attributeNameArrow);
			}
		}
	}

	private void removeArrowFromTarget(Target target, long orbInternalIdArrow, String attributeNameArrow) {
		ArrowCluster arrowCluster = target.arrowClusterCollection.get(orbInternalIdArrow);
		if (arrowCluster != null) {
			arrowCluster.arrows.remove(attributeNameArrow);
		}
	}
	
	public void removeArrows(long orbInternalId, Map<String, List<DecomposedKey>> namesToValues) {
		
		Set<String> attributeNameArrowSet = namesToValues.keySet();
		for (String attributeNameArrow : attributeNameArrowSet) {
			List<DecomposedKey> list = namesToValues.get(attributeNameArrow);
			for (DecomposedKey decomposedKey : list) {
				removeArrows(orbInternalId, attributeNameArrow, decomposedKey);
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
	
	public static class TargetLineup {
		public LinkedHashMap<String, Target> targets = new LinkedHashMap<String, Target>();	
	}
	
	public static class Target {
		public LinkedHashMap<Long, ArrowCluster> arrowClusterCollection = new LinkedHashMap<Long, ArrowCluster>();
	}
	
	public static class ArrowCluster {
		public List<String> arrows = new ArrayList<String>();
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

}
