package com.fletch22.app.designer.appContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.app.App;

@Component
public class AppContainerService extends DomainService<AppContainer, App> {
	
	Logger logger = LoggerFactory.getLogger(AppContainerService.class);
	
	@Autowired
	AppContainerDao appContainerDao;
	
	public AppContainerService() {
		logger.info("Loading appContainerService.");
	}

	public boolean flag = false;
	
	public AppContainer createInstance(String label) {
		AppContainer appContainer = new AppContainer();
		appContainer.label = label;
		save(appContainer);
		
		return appContainer;
	}
	
	public void save(AppContainer appContainer) {
		appContainerDao.save(appContainer);
	}

	public AppContainer get(long orbInternalId) {
		return appContainerDao.read(orbInternalId);
	}

	public AppContainer findByLabel(String defaultAppContainerName) {
		return appContainerDao.findByLabel(defaultAppContainerName);
	}
}
