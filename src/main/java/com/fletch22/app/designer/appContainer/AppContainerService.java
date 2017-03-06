package com.fletch22.app.designer.appContainer;

import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.app.designer.DomainService;

@Component
public class AppContainerService extends DomainService<AppContainer, AppContainerChild> {
	
	Logger logger = LoggerFactory.getLogger(AppContainerService.class);
	
	@Autowired
	AppContainerDao appContainerDao;
	
	public AppContainerService() {
		logger.debug("Loading appContainerService.");
	}

	public boolean flag = false;
	
	public void addToParent(AppContainer appContainer, AppContainerChild appContainerChild) {
		connectParentAndChild(appContainer, appContainerChild);
		save(appContainer);
	}
	
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
	
	@Override
	public AppContainer createInstance(Map<String, String> properties) {
		throw new NotImplementedException("Not yet finished developing");
	}
	
	public AppContainer getDefault() {
		return this.findByLabel(AppDesignerModule.DEFAULT_APP_CONTAINER_NAME);
	}
	
	@Override
	public AppContainer update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, AppContainer.ATTRIBUTE_LIST);
		
		AppContainer appContainer = get(id);
		if (properties.containsKey(AppContainer.ATTR_LABEL)) appContainer.label = properties.get(AppContainer.ATTR_LABEL);
		
		return appContainer;
	}
}
