package com.fletch22.app.designer.appContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.app.App;

@Component
public class AppContainerService extends DomainService {
	
	@Autowired
	AppContainerDao appContainerDao;
	
	public void addToParent(AppContainer appContainer, App app) {
		connectParentAndChild(appContainer, app);
		save(appContainer);
	}

	public AppContainer createInstance(String label) {
		AppContainer appContainer = new AppContainer();
		appContainer.label = label;
		return save(appContainer);
	}
	
	public AppContainer save(AppContainer appContainer) {
		return appContainerDao.save(appContainer);
	}

	public AppContainer get(long orbInternalId) {
		return appContainerDao.read(orbInternalId);
	}
}
