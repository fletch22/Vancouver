package com.fletch22.app.designer.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class AppDao extends AppDesignerDao<App, AppTransformer> {

	Logger logger = LoggerFactory.getLogger(AppDao.class);

	@Autowired
	AppTransformer appTransformer;

	@Override
	protected void create(App app) {
		OrbType orbType = this.orbTypeManager.getOrbType(App.TYPE_LABEL);
		create(app, orbType);
	}

	@Override
	protected AppTransformer getTransformer() {
		return appTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(App app, Orb orb) {
		orb.getUserDefinedProperties().put(App.ATTR_LABEL, app.label);
	}
}
