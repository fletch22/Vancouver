package com.fletch22.app.designer;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.util.StopWatch;

@Component
public class AppContainerDao {
	
	Logger logger = LoggerFactory.getLogger(AppContainerDao.class);

	@Autowired
	QueryManager queryManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;
	
	public AppContainer read(String appContainerLabel) {
		
		AppContainer appContainer = new AppContainer();
		
		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		OrbResultSet orbResultSet = this.queryManager.findByAttribute(orbType.id, AppContainer.ATTR_LABEL, appContainerLabel);
		stopWatch.stop();
		stopWatch.logElapsed();
		
		if (orbResultSet.orbList.size() == 0) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type.");
		}
		appContainer.id = orbResultSet.getOrbList().get(0).getOrbInternalId();
		
		return appContainer;
	}
	
	public AppContainer create(String appContainerLabel) {
		
		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);
		OrbResultSet orbResultSet = this.queryManager.findByAttribute(orbType.id, "label", appContainerLabel);
		
		if (orbResultSet.getOrbList().size() == 0) {
			Orb orb = new Orb();
			orb.setOrbTypeInternalId(orbType.id);
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put(AppContainer.ATTR_LABEL, appContainerLabel);
			orb.setUserDefinedProperties(map);
			
			orbManager.createOrb(orb);
		} else {
			throw new RuntimeException("Encountered problem while tyring to create an AppContainer instance.");
		}
		
		return read(appContainerLabel);
	}
	
	public void update(AppContainer appContainer) {
		
		Orb orb = orbManager.getOrb(appContainer.id);
		
		orb.getUserDefinedProperties().put(appContainer.ATTR_LABEL, appContainer.label);
//		orb.getUserDefinedProperties().put(appContainer.ATTR_APPS, ensureStringified(appContainer.appList));
		
//		orbManager.updateOrb(orb);
		
	}
	
//	private StringBuffer ensureStringified(List<OrbBasedComponent> list) {
//		throw new NotImplementedException("ensureStringified");
//	}
}
