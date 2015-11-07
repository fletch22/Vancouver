package com.fletch22.app.designer.webFolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;

@Component
public class WebFolderService extends DomainService {
	
	@Autowired
	WebFolderDao webFolderDao;

	public void addToParent(WebFolder webFolder, WebFolderChild webFolderChild) {
		connectParentAndChild(webFolder, webFolderChild);
		save(webFolder);
	}

	public WebFolder createInstance(String label) {
		WebFolder webFolder = new WebFolder();
		webFolder.label = label;
		return save(webFolder);
	}
	
	public WebFolder save(WebFolder webFolder) {
		return webFolderDao.save(webFolder);
	}

	public WebFolder get(long orbInternalId) {
		return webFolderDao.read(orbInternalId);
	}
}
