package com.fletch22.orb.cache.reference;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ReferenceUtil {

	public String composeReference(long orbInternalId, String attributeName) {
		return composeReference(orbInternalId) + "^" + attributeName;
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
}

