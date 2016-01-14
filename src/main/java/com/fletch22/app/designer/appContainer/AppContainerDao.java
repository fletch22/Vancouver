package com.fletch22.app.designer.appContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class AppContainerDao extends AppDesignerDao<AppContainer, AppContainerTransformer> {

	Logger logger = LoggerFactory.getLogger(AppContainerDao.class);

	@Autowired
	AppContainerTransformer appContainerTransformer;

	@Override
	public void create(AppContainer appContainer) {

		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);
		
		create(appContainer, orbType);
	}

	public AppContainer read(String appContainerLabel) {

		OrbType orbType = this.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL);

		Orb orb = this.queryManager.findByAttribute(orbType.id, AppContainer.ATTR_LABEL, appContainerLabel).uniqueResult();

		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}
		
		return appContainerTransformer.transform(orb);
	}
	
	@Override
	protected void setNonChildrenAttributes(AppContainer appContainer, Orb orb) {
		orb.getUserDefinedProperties().put(AppContainer.ATTR_LABEL, appContainer.label);
	}

	@Override
	protected AppContainerTransformer getTransformer() {
		return this.appContainerTransformer;
	}
}

