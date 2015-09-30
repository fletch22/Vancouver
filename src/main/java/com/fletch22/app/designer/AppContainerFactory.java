package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.QueryManager;

@Component
public class AppContainerFactory {

	@Autowired
	QueryManager queryManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	public AppContainer getInstance(String appContainerLabel) {
		
		AppContainer appContainer = new AppContainer();
		
		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);
		
		Orb orb = this.queryManager.findDistinctByAttribute(orbType.id, "label", appContainerLabel);
		
		appContainer.id = orb.getOrbInternalId();
		
		return appContainer;
	}
}
