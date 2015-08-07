package com.fletch22.orb;

import java.util.LinkedHashSet;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType;

@Component
public class OrbTypeCloner {

	public OrbType cloneOrb(OrbType orbType) {
		
		LinkedHashSet<String> clonedCustomFields = new LinkedHashSet<String>();
		LinkedHashSet<String> set = orbType.customFields;
		
		if (set != null) {
			for (String property : set) {
				clonedCustomFields.add(property);
			}
		}
		
		return new OrbType(orbType.id, orbType.label, orbType.tranDate, clonedCustomFields);
	}
}
