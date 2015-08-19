package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;

@Component
public class OrbReference {

	private static final char REFERENCE_SEPARATOR = ',';

	@Autowired
	ReferenceCollection referenceCollection;
	
	@Autowired
	OrbManager orbManager;
	
	// TODO Create method to handle removing orb attribute
	public void handleOrbAttributeRemoved(long orbInternalId, String attributeName, String attributeValueOld) {
		
		referenceCollection.removeTarget(orbInternalId, attributeName);
		
		Set<String> referenceValues = getComposedKeys(attributeValueOld);
		for (String referenceValue: referenceValues) {
			DecomposedKey decomposedKey = decomposeKey(referenceValue);
			referenceCollection.removeArrows(orbInternalId, attributeName, decomposedKey);
		}
	}
	
	// TODO Create method to handle removing orb;
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
	
	private String composeReference(long orbInternalId, String attributeName) {
		return ReferenceCollection.REFERENCE_KEY_PREFIX + String.valueOf(orbInternalId) + "^" + attributeName;
	}
	
	private DecomposedKey decomposeKey(String composedKey) {
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

	public void removeReferences(long orbInternalId, String attributeName, String value) {
		
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
}
