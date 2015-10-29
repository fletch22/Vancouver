package com.fletch22.app.designer.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.Website;

@Component
public class AppService {
	
	@Autowired
	AppDao appDao;

	public void addApp(App app, Website webSite) {
		app.getChildren().add(webSite);
		appDao.update(app);
	}

	public App createInstance(String label) {
		App app = new App();
		app.setLabel(label);
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
