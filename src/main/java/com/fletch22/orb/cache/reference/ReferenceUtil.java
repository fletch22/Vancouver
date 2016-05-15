package com.fletch22.orb.cache.reference;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReferenceUtil {
	
	static Logger logger = LoggerFactory.getLogger(ReferenceUtil.class);

	public String composeReference(long orbInternalId, String attributeName) {
		return composeReference(orbInternalId) + ReferenceCollection.ID_ATTRIBUTE_NAME_SEPARATOR + attributeName;
	}
	
	public String composeReference(long orbInternalId) {
		return ReferenceCollection.REFERENCE_KEY_PREFIX + String.valueOf(orbInternalId);
	}

	public StringBuffer composeReferences(Set<String> references) {
		StringBuffer refBuff = new StringBuffer();
		
		int count = 1;
		for (String ref : references) {
			refBuff.append(ref);
			
			if (count != references.size()) {
				refBuff.append(",");	
			}
			count++;
		}
		
		return refBuff;
	}

	public Set<DecomposedKey> convertToKeySet(String references) {

		Set<String> composedKeySet = getComposedKeys(references);
		
		Set<DecomposedKey> decomposedKeySet = new LinkedHashSet<DecomposedKey>();
		for (String key: composedKeySet) {
			decomposedKeySet.add(decomposeKey(key));
		}
		
		return decomposedKeySet;
	}
	
	public boolean isValueAReference(String attributeValue) {
		return (attributeValue == null ? false: attributeValue.startsWith(ReferenceCollection.REFERENCE_KEY_PREFIX));
	}
	
	public boolean isValueAnAttributeReference(String attributeValue) {
		throw new NotImplementedException("Not yet implemented."); //return (attributeValue == null ? false: attributeValue.startsWith(ReferenceCollection.REFERENCE_KEY_PREFIX));
	}
	
	public Set<String> getComposedKeys(String attributeReferenceValue) {
		
		Set<String> set = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(attributeReferenceValue, ",");
		while(st.hasMoreTokens()) {
			set.add(st.nextToken());
		}
		
		return set;
	}
	
	public DecomposedKey decomposeKey(String composedKey) {
		
//		logger.info("Composed Key 1: {}", composedKey);
		
		composedKey = composedKey.substring(ReferenceCollection.REFERENCE_KEY_PREFIX.length());
		
		int index = composedKey.indexOf(ReferenceCollection.ID_ATTRIBUTE_NAME_SEPARATOR);
		
//		logger.info("Composed Key 2: {}", composedKey);
		
		DecomposedKey key = null;
		if (index < 0) {
			key = new DecomposedKey(Long.parseLong(composedKey.substring(0)));
		} else {
			key = new DecomposedKey(Long.parseLong(composedKey.substring(0, index)), composedKey.substring(index + 1));
		}
		
		return key;
	}
}

