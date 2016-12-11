package com.fletch22.app.designer.webFolder;

import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.website.Website;

@Component
public class WebFolderService extends DomainService<WebFolder, WebFolderChild> {
	
	@Autowired
	WebFolderDao webFolderDao;

	public WebFolder createInstance(String label) {
		WebFolder webFolder = new WebFolder();
		webFolder.label = label;
		save(webFolder);
		return webFolder;
	}
	
	public void save(WebFolder webFolder) {
		webFolderDao.save(webFolder);
	}

	public WebFolder get(long orbInternalId) {
		return webFolderDao.read(orbInternalId);
	}
	
	@Override
	public WebFolder createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, WebFolder.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(WebFolder.ATTR_LABEL));
	}
	
	@Override
	public WebFolder update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Website.ATTRIBUTE_LIST);
		
		WebFolder webFolder = get(id);
		if (properties.containsKey(WebFolder.ATTR_LABEL)) webFolder.label = properties.get(WebFolder.ATTR_LABEL);
		
		return webFolder;
	}
}
