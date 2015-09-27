package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.cache.local.ReferenceCollection.ArrowCluster;
import com.fletch22.orb.cache.local.ReferenceCollection.Target;
import com.fletch22.orb.cache.local.ReferenceCollection.TargetLineup;

@Component
@Scope("prototype")
public class OrbReference {

	private static final char REFERENCE_SEPARATOR = ',';

	@Autowired
	ReferenceCollection referenceCollection;
	
	@Autowired
	OrbManager orbManager;
	
	public void removeTarget(long orbInternalId, String attributeName) {
		referenceCollection.removeTarget(orbInternalId, attributeName);
	}
	
	public void handleOrbRemoved(long orbInternalId, Orb orb) {
		
		referenceCollection.removeTarget(orbInternalId);
		
		Map<String, List<DecomposedKey>> nameValuesMap = new HashMap<String, List<DecomposedKey>>();
		Map<String, String> userDefinedProperties = orb.getUserDefinedProperties();
		Set<String> propertyKeySet = userDefinedProperties.keySet();
		for (String property: propertyKeySet) {
			List<DecomposedKey> keys = new ArrayList<DecomposedKey>();
			Set<String> referenceValues = getComposedKeys(userDefinedProperties.get(property));
			for (String referenceValue: referenceValues) {
				keys.add(decomposeKey(referenceValue));
			}
			nameValuesMap.put(property, keys);
		}
		referenceCollection.removeArrows(orbInternalId, nameValuesMap);
	}
	
	public String removeReferenceFromAttributeValue(String attributeValue, String composedReference) {
		Set<String> references = getComposedKeys(attributeValue);
		references.remove(composedReference);
		
		return StringUtils.join(references, REFERENCE_SEPARATOR);
	}
	
	public void addReferenceToAttributeValue(String attributeValue, String composedReference) {
		Set<String> references = getComposedKeys(attributeValue);
		references.add(composedReference);
	}
	
	public String addReference(long orbInternalIdArrow, String attributeNameArrow, String oldValue, long orbInternalIdTarget, String attributeNameTarget) {
		Set<String> references = getComposedKeys(oldValue);
		
		String composedReference = composeReference(orbInternalIdTarget, attributeNameTarget);
		
		if (!references.contains(composedReference)) {
			references.add(composedReference);
			referenceCollection.addReference(orbInternalIdArrow, attributeNameArrow, orbInternalIdTarget, attributeNameTarget);
			oldValue = oldValue + String.valueOf(REFERENCE_SEPARATOR) + composedReference;
		}
		
		return oldValue;
	}
	
	public boolean isValueAReference(String attributeValue) {
		return (attributeValue == null ? false: attributeValue.startsWith(ReferenceCollection.REFERENCE_KEY_PREFIX));
	}
	
	private Set<String> getComposedKeys(String attributeReferenceValue) {
		
		Set<String> set = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(attributeReferenceValue, ",");
		while(st.hasMoreTokens()) {
			set.add(st.nextToken());
		}
		
		return set;
	}
	
	public String composeReference(long orbInternalId, String attributeName) {
		return ReferenceCollection.REFERENCE_KEY_PREFIX + String.valueOf(orbInternalId) + "^" + attributeName;
	}
	
	public DecomposedKey decomposeKey(String composedKey) {
		DecomposedKey key = new DecomposedKey();
		
		composedKey = composedKey.substring(ReferenceCollection.REFERENCE_KEY_PREFIX.length());
		
		int index = composedKey.indexOf(ReferenceCollection.ID_ATTRIBUTE_NAME_SEPARATOR);
		
		key.orbInternalId = Long.parseLong(composedKey.substring(0, index));
		key.attributeName = composedKey.substring(index + 1);
		
		return key;
	}

	public static class DecomposedKey {
		public long orbInternalId;
		public String attributeName;
	}

	public void removeArrowsFromIndex(long orbInternalId, String attributeName, String value) {
		
		List<DecomposedKey> keys = convertToDecomposedKeys(value);
		referenceCollection.removeArrows(orbInternalId, attributeName, keys);
	}

	public void addReferences(long orbInternalId, String attributeName, String value) {
		List<DecomposedKey> keys = convertToDecomposedKeys(value);
		referenceCollection.addReferences(orbInternalId, attributeName, keys);
	}

	private List<DecomposedKey> convertToDecomposedKeys(String value) {
		List<DecomposedKey> keys = new ArrayList<DecomposedKey>();
		Set<String> referenceValues = getComposedKeys(value);
		for (String referenceValue: referenceValues) {
			keys.add(decomposeKey(referenceValue));
		}
		return keys;
	}

	public Map<Long, AttributeArrows> getArrowsPointingAtTarget(Orb orb) {
		Map<Long, AttributeArrows> attributeArrowMap = new HashMap<Long, AttributeArrows>();

		TargetLineup targetLineup = this.referenceCollection.targetLineups.get(orb.getOrbInternalId());
		
		if (targetLineup != null) {
			Set<String> targetKeys = targetLineup.targets.keySet();
			
			for (String attributeName: targetKeys) {
				Target target = targetLineup.targets.get(attributeName);
				if (target != null) {
					
					LinkedHashMap<Long, ArrowCluster> arrowClusterCollection = target.arrowClusterCollection;
					Set<Long> arrowKeys = arrowClusterCollection.keySet();
					for (long arrow: arrowKeys) {
						
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

	public void ensureOrbsArrowsRemoved(Orb orb) {
		Set<String> attributeKeys = orb.getUserDefinedProperties().keySet();
		for (String attributeName: attributeKeys) {
			String value = orb.getUserDefinedProperties().get(attributeName);
			if (isValueAReference(value)) {
				removeArrowsFromIndex(orb.getOrbInternalId(), attributeName, value);
			}
		}
	}
	
	public static class AttributeArrows {
		public List<String> attributesContainingArrows;
	}

	public void clear() {
		referenceCollection.clear();
	}

	public int countArrowsPointToTarget(Orb orb) {
		return getArrowsPointingAtTarget(orb).size();
	}
}
