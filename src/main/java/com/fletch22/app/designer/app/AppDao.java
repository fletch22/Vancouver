package com.fletch22.app.designer.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
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

		OrbType orbType = ensureInstanceUnique(App.TYPE_LABEL, App.ATTR_LABEL, app.label);
		
		Orb orb = craftProtoOrb(app, orbType);
		
		orb.getUserDefinedProperties().put(App.ATTR_LABEL, app.label);

		orb = orbManager.createOrb(orb);

		return appContainerTransformer.transform(orb);
	}

	public void update(App app) {

		Orb orbToUpdate = orbManager.getOrb(app.getId());

		orbToUpdate.getUserDefinedProperties().put(App.ATTR_LABEL, app.label);
		
		this.setOrbChildrenAttribute(app, orbToUpdate);

		orbManager.updateOrb(orbToUpdate);
	}

	public OrbBasedComponent get(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return appContainerTransformer.transform(orb);
	}
}
