package com.fletch22.orb.cache.external;

import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;

@Component
public class NakedToClothedOrbTransformer {

	public Orb convertNakedToClothed(NakedOrb nakedOrb) {
		
//		long internalId = new Long(nakedOrb.getOrbInternalId()).longValue();
//		long internalTypeId = new Long(nakedOrb.getOrbTypeInternalId()).longValue();
//		
//		BigDecimal tranDate = new BigDecimal(nakedOrb.getTranDate());
//		
//		return new Orb(internalId, internalTypeId, tranDate, nakedOrb.getUserDefinedProperties());
		throw new RuntimeException("Deprecated.");
	}
}
