package com.fletch22.app.designer.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.website.Website;

@Component
public class AppService extends DomainService<App, Website> {
	
	@Autowired
	AppDao appDao;

	public void addToParent(App app, Website website) {
		connectParentAndChild(app, website);
		save(app);
	}
	
	public App createInstance(String label) {
		App app = new App();
		app.label = label;
		save(app);
		
		return app;
	}
	
	public void save(App app) {
		appDao.save(app);
	}
	
	public App get(long orbInternalId) {
		return appDao.read(orbInternalId);
	}
}
