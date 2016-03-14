package com.fletch22.app.designer.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;

@Component
public class DomainUtilService {
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;

	public String getTypeLabelFromId(long id) { 
		
		Orb orb = orbManager.getOrb(id);
		
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		
		return orbType.label;
	}
}
