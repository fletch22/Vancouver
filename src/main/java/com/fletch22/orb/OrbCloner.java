package com.fletch22.orb;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class OrbCloner {

	public Orb cloneOrb(Orb orb) {
		Orb clone = new Orb();
		
		clone.setOrbTypeInternalId(orb.getOrbTypeInternalId());
		clone.setOrbInternalId(orb.getOrbInternalId());
		
		Map<String, String> properties = orb.getUserDefinedProperties();
		Set<String> keySet = properties.keySet();
		for (String key : keySet) {
			 clone.getUserDefinedProperties().put(key, properties.get(key));
		}
		
		return clone;
	}
}
