package com.fletch22.app.designer;

import java.util.LinkedHashMap;

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

@Component
public class AppContainerDao extends AppDesignerDao {

	Logger logger = LoggerFactory.getLogger(AppContainerDao.class);

	@Autowired
	QueryManager queryManager;

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	OrbManager orbManager;

	public AppContainer create(String appContainerLabel) {

		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);
		OrbResultSet orbResultSet = this.queryManager.findByAttribute(orbType.id, AppContainer.ATTR_LABEL, appContainerLabel);

		if (orbResultSet.getOrbList().size() == 0) {
			Orb orb = new Orb();
			orb.setOrbTypeInternalId(orbType.id);
			orb.getUserDefinedProperties().put(AppContainer.ATTR_LABEL, appContainerLabel);

			orbManager.createOrb(orb);
		} else {
			throw new RuntimeException("Encountered problem while tyring to create an AppContainer instance.");
		}

		return read(appContainerLabel);
	}

	public AppContainer read(String appContainerLabel) {

		AppContainer appContainer = new AppContainer();

		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);

		Orb orb = this.queryManager.findByAttribute(orbType.id, AppContainer.ATTR_LABEL, appContainerLabel).uniqueResult();

		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}
		appContainer.setId(orb.getOrbInternalId());
		appContainer.setLabel(appContainerLabel);
		
		// TODO: 10-24-2015: Convert refs to objects and add to container.

		return appContainer;
	}

	public void update(AppContainer appContainer) {

		Orb orb = orbManager.getOrb(appContainer.getId());

		orb.getUserDefinedProperties().put(AppContainer.ATTR_LABEL, appContainer.label);
		
		// TODO: 10-24-2015: Add guard against bad types being added.
		orb.getUserDefinedProperties().put(AppContainer.ATTR_APPS, convertToReferences(appContainer.getChildren()).toString());

		orbManager.updateOrb(orb);
	}

	public void delete(long id) {
		orbManager.deleteOrb(id, false);
	}
}
