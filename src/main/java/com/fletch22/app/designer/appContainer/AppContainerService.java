package com.fletch22.app.designer.appContainer;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppDao;

@Component
public class AppContainerService extends DomainService {
	
	@Autowired
	AppContainerDao appContainerDao;
	
	@Autowired
	AppDao appDao;

	public void addToParent(AppContainer appContainer, App app) {
		connectParentAndChild(appContainer, app);
		update(appContainer);
	}

	public AppContainer createInstance(String label) {
		AppContainer appContainer = new AppContainer();
		appContainer.label = label;
		return appContainerDao.create(appContainer);
	}
	
	public AppContainer update(AppContainer appContainer) {
		appContainerDao.update(appContainer);
		return appContainer;
	}

	public AppContainer get(long orbInternalId) {
		return appContainerDao.read(orbInternalId);
	}
}
