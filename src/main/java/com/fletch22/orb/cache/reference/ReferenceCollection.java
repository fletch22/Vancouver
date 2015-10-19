package com.fletch22.orb.cache.reference;

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
public class ReferenceCollection {
	
	Logger logger = LoggerFactory.getLogger(ReferenceCollection.class);

	public static final String REFERENCE_KEY_PREFIX = "^^^";
	public static final String ID_ATTRIBUTE_NAME_SEPARATOR = "^";
	
	@Autowired
	AttributeReferenceCollection attributeReferenceCollection;
	
	@Autowired
	OrbReferenceCollection orbReferenceCollection;
	
	public void addReferences(long arrowOrbInternalId, String arrowAttributeName, List<DecomposedKey> targets) {
		
		for (DecomposedKey key: targets) {
			addReference(arrowOrbInternalId, arrowAttributeName, key);
		}
	}
	
	public void addReference(long arrowOrbInternalId, String arrowAttributeName, DecomposedKey targetKey) {
		if (targetKey.isKeyPointingToAttribute()) {
			attributeReferenceCollection.addReference(arrowOrbInternalId, arrowAttributeName, targetKey.getOrbInternalId(), targetKey.getAttributeName());
		} else {
			orbReferenceCollection.addReference(arrowOrbInternalId, arrowAttributeName, targetKey.getOrbInternalId());
		}
	}
	
	protected int countArrows() {
		return attributeReferenceCollection.countArrows() + orbReferenceCollection.countArrows(); 
	}
	
	protected int countArrowsPointingToTargetAttribute(long orbInternalIdTarget, String attributeTargetName) {
		return attributeReferenceCollection.countArrowsPointingToTarget(orbInternalIdTarget, attributeTargetName);
	}
	
	public void removeArrows(long orbInternalIdArrow, String attributeNameArrow, List<DecomposedKey> keys) {
		
		for (DecomposedKey key: keys) {
			if (key.isKeyPointingToAttribute()) {
				attributeReferenceCollection.removeArrowsFromRefs(orbInternalIdArrow, attributeNameArrow, key);
			} else {
				orbReferenceCollection.removeArrowsFromRefs(orbInternalIdArrow, attributeNameArrow, key);
			}
		}
	}

	public void removeArrows(long orbInternalId, Map<String, List<DecomposedKey>> namesToValuesMap) {
		
		Set<String> attributeNameArrowSet = namesToValuesMap.keySet();
		for (String attributeNameArrow : attributeNameArrowSet) {
			List<DecomposedKey> list = namesToValuesMap.get(attributeNameArrow);
			removeArrows(orbInternalId, attributeNameArrow, list);
		}
	}
	
	public void removeTarget(long orbTargetInternalId) {
		attributeReferenceCollection.removeTarget(orbTargetInternalId);
		orbReferenceCollection.removeTarget(orbTargetInternalId);
	}
	
	public void removeTargetAttribute(long orbTargetInternalId, String attributeNameTarget) {
		attributeReferenceCollection.removeTarget(orbTargetInternalId, attributeNameTarget);
	}
	
	public void renameAttributeReference(long orbInternalId, String attributeNameOld, String attributeNameNew) {
		attributeReferenceCollection.renameAttribute(orbInternalId, attributeNameOld, attributeNameNew);
		orbReferenceCollection.renameAttribute(orbInternalId, attributeNameOld, attributeNameNew);
	}

	public void clear() {
		attributeReferenceCollection.clear();
		orbReferenceCollection.clear();
	}
	
	public Map<Long, AttributeArrows> getAttributeReferencesPointingAtTarget(long orbInternalIdTarget) {
		Map<Long, AttributeArrows> attributeArrowsMap = this.attributeReferenceCollection.getArrowsPointingAtTarget(orbInternalIdTarget);
		Map<Long, AttributeArrows> orbRefAttributeArrows = this.orbReferenceCollection.getArrowsPointingAtTarget(orbInternalIdTarget);
		
		return combineAttributeArrowMaps(attributeArrowsMap, orbRefAttributeArrows);
	}

	private Map<Long, AttributeArrows> combineAttributeArrowMaps(Map<Long, AttributeArrows> attributeArrowsMap, Map<Long, AttributeArrows> orbRefAttributeArrows) {
		
		Set<Long> orbRefKeySet = orbRefAttributeArrows.keySet();
		for (Long orbInternalId : orbRefKeySet) {
			AttributeArrows attributeArrowsOrbRef = orbRefAttributeArrows.get(orbInternalId);
			
			boolean isEntryExist = attributeArrowsMap.containsKey(orbInternalId);
			if (isEntryExist) {
				AttributeArrows attributeArrowsDoesExist = attributeArrowsMap.get(orbInternalId);
				attributeArrowsDoesExist.attributesContainingArrows.addAll(attributeArrowsOrbRef.attributesContainingArrows);
			} else {
				attributeArrowsMap.put(orbInternalId, attributeArrowsOrbRef);
			}
		}
		return attributeArrowsMap;
	}

	public void removeReference(long arrowOrbInternalId, String arrowAttributeName, DecomposedKey decomposedKey) {
		if (decomposedKey.isKeyPointingToAttribute()) {
			attributeReferenceCollection.removeArrowsFromRefs(arrowOrbInternalId, arrowAttributeName, decomposedKey);
		} else {
			orbReferenceCollection.removeArrowsFromRefs(arrowOrbInternalId, arrowAttributeName, decomposedKey);
		}
	}

}
