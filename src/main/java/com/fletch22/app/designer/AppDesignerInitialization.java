package com.fletch22.app.designer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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
		
		LinkedHashSet<String> fields = new LinkedHashSet<String>();
		fields.add(AppContainer.ATTR_LABEL);
		fields.add(AppContainer.ATTR_APPS);
		long orbTypeInternalId = orbTypeManager.createOrbType(AppContainer.TYPE_LABEL, fields);
		
		Orb orb = orbManager.createOrb(orbTypeInternalId);
		orbManager.deleteOrb(orb.getOrbInternalId(), false);
		
		this.queryManager.findByAttribute(orbTypeInternalId, AppContainer.ATTR_LABEL, "joe");
	}
}
