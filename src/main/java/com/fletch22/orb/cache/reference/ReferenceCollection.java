package com.fletch22.orb.cache.reference;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
	
	public void addReferences(long orbInternalId, String attributeName, List<DecomposedKey> keys) {
		attributeReferenceCollection.addReferences(orbInternalId, attributeName, keys);
	}
	
	protected int countArrows() {
		return attributeReferenceCollection.countArrows();
	}
	
	protected int countArrowsPointingToTarget(long orbInternalIdTarget, String attributeName) {
		return attributeReferenceCollection.countArrowsPointingToTarget(orbInternalIdTarget, attributeName);
	}
	
	public void removeArrows(long orbInternalIdArrow, String attributeNameArrow, List<DecomposedKey> keys) {
		attributeReferenceCollection.removeArrows(orbInternalIdArrow, attributeNameArrow, keys);
	}

	public void removeArrows(long orbInternalId, Map<String, List<DecomposedKey>> namesToValuesMap) {
		attributeReferenceCollection.removeArrows(orbInternalId, namesToValuesMap);
	}
	
	public void removeTarget(long orbTargetInternalId) {
		attributeReferenceCollection.removeTarget(orbTargetInternalId);
	}
	
	public void removeTarget(long orbTargetInternalId, String attributeNameTarget) {
		attributeReferenceCollection.removeTarget(orbTargetInternalId, attributeNameTarget);
	}
	
	public void renameAttributeReference(long orbInternalId, String attributeNameOld, String attributeNameNew) {
		attributeReferenceCollection.renameAttribute(orbInternalId, attributeNameOld, attributeNameNew);
	}

	public void clear() {
		attributeReferenceCollection.targetLineups.clear();
	}
	
	public Map<Long, AttributeArrows> getAttributeReferencesPointingAtTarget(long orbInternalIdTarget) {
		return attributeReferenceCollection.getArrowsPointingAtTarget(orbInternalIdTarget);
	}

}
