package com.fletch22.app.designer.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.website.Website;

@Component
public class AppService extends DomainService {
	
	@Autowired
	AppDao appDao;

	public void addToParent(App app, Website website) {
		connectParentAndChild(app, website);
		update(app);
	}
	
	public App createInstance(String label) {
		App app = new App();
		app.label = label;
		return appDao.create(app);
	}
	
	public App update(App app) {
		appDao.update(app);
		return app;
	}

	public OrbBasedComponent get(long orbInternalId) {
		return appDao.get(orbInternalId);
	}
}
