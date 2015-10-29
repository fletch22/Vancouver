package com.fletch22.app.designer.appContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.query.QueryManager;

@Component
public class AppContainerDao extends AppDesignerDao<AppContainerDao> {

	Logger logger = LoggerFactory.getLogger(AppContainerDao.class);

	@Autowired
	QueryManager queryManager;

	@Autowired
	AppContainerTransformer appContainerTransformer;

	public AppContainer create(AppContainer appContainer) {

		OrbType orbType = ensureInstanceUnique(AppContainer.TYPE_LABEL, AppContainer.ATTR_LABEL, appContainer.label);
		
		Orb orb = new Orb();
		orb.setOrbTypeInternalId(orbType.id);
		orb.getUserDefinedProperties().put(AppContainer.ATTR_LABEL, appContainer.label);

		orb = orbManager.createOrb(orb);

		return appContainerTransformer.transform(orb);
	}
	
	public AppContainer read(String appContainerLabel) {

		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);

		Orb orb = this.queryManager.findByAttribute(orbType.id, AppContainer.ATTR_LABEL, appContainerLabel).uniqueResult();

		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}
		
		return appContainerTransformer.transform(orb);
	}
	
	public AppContainer read(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return appContainerTransformer.transform(orb);
	}

	public void update(AppContainer appContainer) {

		Orb orb = getOrbMustExist(appContainer.getId());

		orb.getUserDefinedProperties().put(AppContainer.ATTR_LABEL, appContainer.label);
		
		// TODO: 10-24-2015: Add guard against bad types being added.
		orb.getUserDefinedProperties().put(AppContainer.ATTR_APPS, convertToReferences(appContainer.getChildren()).toString());

		orbManager.updateOrb(orb);
	}
}

