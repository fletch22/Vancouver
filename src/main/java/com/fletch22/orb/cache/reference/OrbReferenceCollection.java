package com.fletch22.orb.cache.reference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.AttributeArrows;

@Component
@Scope("prototype")
public class OrbReferenceCollection {
static Logger logger = LoggerFactory.getLogger(AttributeReferenceCollection.class);

	@Autowired
	ReferenceUtil referenceUtil;

	public Map<Long, OrbRefTarget> orbReferences = new HashMap<Long, OrbRefTarget>(); 

	public void addReferences(long orbInternalId, String attributeName, List<DecomposedKey> keys) {
		for (DecomposedKey key: keys) {
			addReference(orbInternalId, attributeName, key.getOrbInternalId());
		}
	}
	
	public void addReference(long orbInternalIdArrow, String nameOfArrowAttribute, long orbInternalIdTarget) {
		
		OrbRefTarget orbTarget = orbReferences.get(orbInternalIdTarget);
		
		if (orbTarget == null) {
			orbTarget = new OrbRefTarget();
			orbReferences.put(orbInternalIdTarget, orbTarget);
		}
		
		OrbRefArrowCluster orbRefArrowCluster = orbTarget.orbRefArrowClusterMap.get(orbInternalIdArrow);
			
		if (orbRefArrowCluster == null) {
			orbRefArrowCluster = new OrbRefArrowCluster();
			orbTarget.orbRefArrowClusterMap.put(orbInternalIdArrow, orbRefArrowCluster);
		}
		
		orbRefArrowCluster.arrows.add(nameOfArrowAttribute);
	}
	
	public static class OrbRefTarget {
		public Map<Long, OrbRefArrowCluster> orbRefArrowClusterMap = new HashMap<Long, OrbRefArrowCluster>();
	}
	
	public static class OrbRefArrowCluster {
		Set<String> arrows = new HashSet<String>();
	}
	
	protected int countArrows() {
		
		int count = 0;
		Set<Long> orbReferencesSet = orbReferences.keySet();
		for (long orbInternalIdTarget: orbReferencesSet) {
			 count += countArrowsPointingToTarget(orbInternalIdTarget);
		}
		
		return count;
	}
	
	protected int countArrowsPointingToTarget(long orbInternalIdTarget) {
		
		int count = 0;
		OrbRefTarget orbRefTarget = orbReferences.get(orbInternalIdTarget);
		if (orbRefTarget != null) {
			
			Set<Long> orbInternalIdArrowSet = orbRefTarget.orbRefArrowClusterMap.keySet();
			for (Long orbInternalIdArrow: orbInternalIdArrowSet) {
				OrbRefArrowCluster orbRefArrowCluster = orbRefTarget.orbRefArrowClusterMap.get(orbInternalIdArrow);
				
				count += orbRefArrowCluster.arrows.size();
			}
		}
		
		return count;
	}
	
	public void removeArrows(long orbInternalIdArrow, String attributeNameArrow, List<DecomposedKey> keyList) {
		for (DecomposedKey key: keyList) {
			removeArrowsFromRefs(orbInternalIdArrow, attributeNameArrow, key);
		}
	}

	public void removeArrowsFromRefs(long orbInternalIdArrow, String attributeNameArrow, DecomposedKey decomposedKey) {
		OrbRefTarget orbRefTarget = orbReferences.get(decomposedKey.getOrbInternalId());
		
		if (orbRefTarget != null) {
			removeArrowFromOrbRefTarget(orbRefTarget, orbInternalIdArrow, attributeNameArrow);
			if (orbRefTarget.orbRefArrowClusterMap.size() == 0) {
				orbReferences.remove(decomposedKey.getOrbInternalId());
			}
		}
	}
	
	private void removeArrowFromOrbRefTarget(OrbRefTarget orbRefTarget, long orbInternalIdArrow, String attributeNameArrow) {
		OrbRefArrowCluster orbRefArrowCluster = orbRefTarget.orbRefArrowClusterMap.get(orbInternalIdArrow);
		
		orbRefArrowCluster.arrows.remove(attributeNameArrow);
		if (orbRefTarget.orbRefArrowClusterMap.size() == 0) {
			orbRefTarget.orbRefArrowClusterMap.remove(attributeNameArrow);
		}
	}

	public void removeArrows(long orbInternalId, Map<String, List<DecomposedKey>> namesToValuesMap) {
		
		Set<String> attributeNameArrowSet = namesToValuesMap.keySet();
		for (String attributeNameArrow : attributeNameArrowSet) {
			List<DecomposedKey> list = namesToValuesMap.get(attributeNameArrow);
			for (DecomposedKey decomposedKey : list) {
				removeArrowsFromRefs(orbInternalId, attributeNameArrow, decomposedKey);
			}
		}
	}
	
	public void removeTarget(long orbTargetInternalId) {
		orbReferences.remove(orbTargetInternalId);
	}
	
	public void renameAttribute(long orbInternalId, String attributeNameOld, String attributeNameNew) {
		renameArrows(orbInternalId, attributeNameOld, attributeNameNew);
	}

	private void renameArrows(long orbInternalId, String attributeNameOld, String attributeNameNew) {
		Set<Long> orbRefTargetKeySet = orbReferences.keySet();
		for (long orbInternalIdTarget: orbRefTargetKeySet) {
			OrbRefTarget orbRefTarget = orbReferences.get(orbInternalIdTarget);
			
			Set<Long> orbRefArrowClusterKeySet = orbRefTarget.orbRefArrowClusterMap.keySet();
			for (Long orbInternalIdArrow: orbRefArrowClusterKeySet) {
				OrbRefArrowCluster orbRefArrowCluster = orbRefTarget.orbRefArrowClusterMap.get(orbInternalIdArrow);
				
				if (orbRefArrowCluster.arrows.contains(attributeNameOld)) {
					orbRefArrowCluster.arrows.remove(attributeNameOld);
					orbRefArrowCluster.arrows.remove(attributeNameNew);
				}
			}
		}
	}

	public void clear() {
		orbReferences.clear();
	}
	
	public Map<Long, AttributeArrows> getArrowsPointingAtTarget(long orbInternalIdTarget) {
		Map<Long, AttributeArrows> attributeArrowMap = new HashMap<Long, AttributeArrows>();

		OrbRefTarget orbRefTarget = this.orbReferences.get(orbInternalIdTarget);
		
		if (orbRefTarget != null) {
			Set<Long> clusterMapKeySet = orbRefTarget.orbRefArrowClusterMap.keySet();
			
			for (Long orbInternalIdArrow: clusterMapKeySet) {
				OrbRefArrowCluster orbRefArrowCluster = orbRefTarget.orbRefArrowClusterMap.get(orbInternalIdArrow);
				
				if (orbRefArrowCluster != null && orbRefArrowCluster.arrows.size() > 0) {
					
					AttributeArrows attributeArrows = attributeArrowMap.get(orbInternalIdArrow);
					if (attributeArrows == null) {
						attributeArrows = new AttributeArrows();
						attributeArrowMap.put(orbInternalIdArrow, attributeArrows);
					}
					attributeArrows.attributesContainingArrows.addAll(orbRefArrowCluster.arrows);
				}
			}
		}
		
		return attributeArrowMap;
	}
}
