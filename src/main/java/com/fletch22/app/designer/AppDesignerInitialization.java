package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.QueryManager;

@Component
public class AppDesignerInitialization {
	
	@Autowired
	OrbManager orbManager;

	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	QueryManager queryManager;
	
	public void initialize() {
		
		long orbTypeInternalId = orbTypeManager.createOrbType(AppContainer.TYPE_LABEL, AppContainer.ATTRIBUTE_LIST);
		
		// TODO: 10-24-2015: Primes the indexes. Need to do this without logging.
		Orb orb = orbManager.createOrb(orbTypeInternalId);
		orbManager.deleteOrb(orb.getOrbInternalId(), false);
		this.queryManager.findByAttribute(orbTypeInternalId, AppContainer.ATTR_LABEL, "joe");
	}
}
