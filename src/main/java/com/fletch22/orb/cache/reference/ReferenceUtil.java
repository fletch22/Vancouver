package com.fletch22.orb.cache.reference;

import org.springframework.stereotype.Component;

@Component
public class ReferenceUtil {

	public String composeReference(long orbInternalId, String attributeName) {
		return ReferenceCollection.REFERENCE_KEY_PREFIX + String.valueOf(orbInternalId) + "^" + attributeName;
	}
	
}
