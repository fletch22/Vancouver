package com.fletch22.orb.cache.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.cache.local.AttributeArrows;

@Component
@Scope("prototype")
public class OrbReference {
	
	static Logger logger = LoggerFactory.getLogger(OrbReference.class);

	private static final char REFERENCE_SEPARATOR = ',';

	@Autowired
	public ReferenceCollection referenceCollection;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	ReferenceUtil referenceUtil;
	
	public void removeTargetAttribute(long orbInternalId, String attributeName) {
		referenceCollection.removeTargetAttribute(orbInternalId, attributeName);
	}
	
	public void handleOrbRemoved(long orbInternalId, Orb orb) {
		
		referenceCollection.removeTarget(orbInternalId);
		
		Map<String, List<DecomposedKey>> nameValuesMap = new HashMap<String, List<DecomposedKey>>();
		Map<String, String> userDefinedProperties = orb.getUserDefinedProperties();
		Set<String> propertyKeySet = userDefinedProperties.keySet();
		for (String property: propertyKeySet) {
			List<DecomposedKey> keys = new ArrayList<DecomposedKey>();
			Set<String> referenceValues = referenceUtil.getComposedKeys(userDefinedProperties.get(property));
			for (String referenceValue: referenceValues) {
				keys.add(referenceUtil.decomposeKey(referenceValue));
			}
			nameValuesMap.put(property, keys);
		}
		referenceCollection.removeArrows(orbInternalId, nameValuesMap);
	}
	
//	public String removeReferenceFromAttributeValue(String attributeValue, String composedReference) {
//		Set<String> references = getComposedKeys(attributeValue);
//		references.remove(composedReference);
//		
//		return StringUtils.join(references, REFERENCE_SEPARATOR);
//	}
//	
//	public void addReferenceToAttributeValue(String attributeValue, String composedReference) {
//		Set<String> references = getComposedKeys(attributeValue);
//		references.add(composedReference);
//	}
	
	public StringBuffer addReference(long orbInternalIdArrow, String attributeNameArrow, StringBuffer oldValue, long orbInternalIdTarget, String attributeNameTarget) {
		Set<String> references = referenceUtil.getComposedKeys(oldValue.toString());
		
		String composedReference = referenceUtil.composeReference(orbInternalIdTarget, attributeNameTarget);
		
		if (!references.contains(composedReference)) {
			DecomposedKey decomposedKey = new DecomposedKey(orbInternalIdTarget, attributeNameTarget);
			referenceCollection.addReference(orbInternalIdArrow, attributeNameArrow, decomposedKey);
			
			if (!StringUtils.isBlank(oldValue)) {
				oldValue.append(REFERENCE_SEPARATOR);
			}
			oldValue.append(composedReference);
		}
		
		return oldValue;
	}
	
	public StringBuffer addReference(long orbInternalIdArrow, String attributeNameArrow, StringBuffer oldValue, long orbInternalIdTarget) {
		Set<String> references = referenceUtil.getComposedKeys(oldValue.toString());
		
		String composedReference = referenceUtil.composeReference(orbInternalIdTarget);
		
		if (!references.contains(composedReference)) {
			DecomposedKey decomposedKey = new DecomposedKey(orbInternalIdTarget);
			referenceCollection.addReference(orbInternalIdArrow, attributeNameArrow, decomposedKey);
			
			if (!StringUtils.isBlank(oldValue)) {
				oldValue.append(REFERENCE_SEPARATOR);
			}
			oldValue.append(composedReference);
		}
		
		return oldValue;
	}
	
	public StringBuffer removeReference(long arrowOrbInternalId, String arrowAttributeName, StringBuffer oldValue, long targetOrbInternalId) {
		Set<String> references = referenceUtil.getComposedKeys(oldValue.toString());
		
		String composedReference = referenceUtil.composeReference(targetOrbInternalId);
		
		StringBuffer newValue = new StringBuffer();
		if (references.contains(composedReference)) {
			DecomposedKey decomposedKey = new DecomposedKey(targetOrbInternalId);
			referenceCollection.removeReference(arrowOrbInternalId, arrowAttributeName, decomposedKey);
			references.remove(composedReference);
			newValue = referenceUtil.composeReferences(references);
		}
		
		return newValue;
	}
	
	public StringBuffer removeReference(long arrowOrbInternalId, String arrowAttributeName, StringBuffer oldValue, long targetOrbInternalId, String targetAttributeName) {
		Set<String> references = referenceUtil.getComposedKeys(oldValue.toString());
		
		String composedReference = referenceUtil.composeReference(targetOrbInternalId, targetAttributeName);
		
		StringBuffer newValue = new StringBuffer();
		if (references.contains(composedReference)) {
			DecomposedKey decomposedKey = new DecomposedKey(targetOrbInternalId, targetAttributeName);
			referenceCollection.removeReference(arrowOrbInternalId, arrowAttributeName, decomposedKey);
			references.remove(composedReference);
			newValue = referenceUtil.composeReferences(references);
		}
		
		return newValue;
	}
	
	public void removeArrowsFromIndex(long orbInternalId, String attributeName, String value) {
		
		logger.debug("rafi: {}", value);
		
		List<DecomposedKey> keys = convertToDecomposedKeys(value);
		referenceCollection.removeArrows(orbInternalId, attributeName, keys);
	}

	public void addReferences(long orbInternalId, String attributeName, String value) {
		List<DecomposedKey> keys = convertToDecomposedKeys(value);
		referenceCollection.addReferences(orbInternalId, attributeName, keys);
	}

	private List<DecomposedKey> convertToDecomposedKeys(String value) {
		List<DecomposedKey> keys = new ArrayList<DecomposedKey>();
		
		logger.debug("CTDK: {}", value);	
		
		Set<String> referenceValues = referenceUtil.getComposedKeys(value);
		for (String referenceValue: referenceValues) {
			keys.add(referenceUtil.decomposeKey(referenceValue));
		}
		return keys;
	}

	public Map<Long, AttributeArrows> getArrowsPointingAtTarget(Orb orb) {
		return referenceCollection.getAttributeReferencesPointingAtTarget(orb.getOrbInternalId());
	}

	public void ensureOrbsArrowsRemoved(Orb orb) {
		Set<String> attributeKeys = orb.getUserDefinedProperties().keySet();
		for (String attributeName: attributeKeys) {
			String value = orb.getUserDefinedProperties().get(attributeName);
			if (referenceUtil.isValueAReference(value)) {
				removeArrowsFromIndex(orb.getOrbInternalId(), attributeName, value);
			}
		}
	}

	public void clear() {
		referenceCollection.clear();
	}

	public int countArrowsPointToTarget(Orb orb) {
		return getArrowsPointingAtTarget(orb).size();
	}

}
