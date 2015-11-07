package com.fletch22.app.designer.webFolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;

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
}
