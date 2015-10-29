package com.fletch22.app.designer.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.AppDesignerDao;
import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;

@Component
public class AppDao extends AppDesignerDao {

	Logger logger = LoggerFactory.getLogger(AppDao.class);

	@Autowired
	QueryManager queryManager;

	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	AppTransformer appContainerTransformer;

	public App create(App app) {

		OrbType orbType = this.orbTypeManager.getOrbType(App.TYPE_LABEL);
		OrbResultSet orbResultSet = this.queryManager.findByAttribute(orbType.id, App.ATTR_LABEL, app.getLabel());

		Orb orb = null;
		if (orbResultSet.getOrbList().size() == 0) {
			orb = new Orb();
			orb.setOrbTypeInternalId(orbType.id);
			orb.getUserDefinedProperties().put(App.ATTR_LABEL, app.getLabel());

			orb = orbManager.createOrb(orb);
		} else {
			throw new RuntimeException("Encountered problem while tyring to create an AppContainer instance.");
		}

		return appContainerTransformer.transform(orb);
	}

	public App read(String appLabel) {

		OrbType orbType = this.orbTypeManager.getOrbType(App.TYPE_LABEL);

		Orb orb = this.queryManager.findByAttribute(orbType.id, App.ATTR_LABEL, appLabel).uniqueResult();

		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}
		
		return appContainerTransformer.transform(orb);
	}

	public void update(App app) {

		Orb orb = orbManager.getOrb(app.getId());

		orb.getUserDefinedProperties().put(App.ATTR_LABEL, app.getLabel());
		
		orb.getUserDefinedProperties().put(App.ATTR_WEBSITES, convertToReferences(app.getChildren()).toString());

		orbManager.updateOrb(orb);
	}

	public OrbBasedComponent get(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return appContainerTransformer.transform(orb);
	}
}
