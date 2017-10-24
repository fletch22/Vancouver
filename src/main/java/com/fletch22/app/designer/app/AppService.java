package com.fletch22.app.designer.app;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.website.Website;

@Component
public class AppService extends DomainService<App, Website> {
	
	@Autowired
	AppDao appDao;

	public void addToParent(App app, Website website) {
		connectParentAndChild(app, website, Child.ORDINAL_LAST);
		save(app);
	}
	
	public App createInstance(String label) {
		App app = new App();
		app.label = label;
		save(app);
		
		return app;
	}
	
	@Override
	public App createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, App.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(App.ATTR_LABEL));
	}
	
	public App update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, App.ATTRIBUTE_LIST);
		
		App app = get(id);
		app.label = properties.get(App.ATTR_LABEL);
		
		this.save(app);
		
		return app;
	}
	
	public void save(App app) {
		appDao.save(app);
	}
	
	public App get(long orbInternalId) {
		return appDao.read(orbInternalId);
	}
}
