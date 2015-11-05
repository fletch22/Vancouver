package com.fletch22.app.designer.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.webFolder.WebFolderChild;

@Component
public class WebsiteService extends DomainService {
	
	@Autowired
	WebsiteDao websiteDao;

	public void addToParent(Website website, WebFolderChild webFolderChild) {
		connectParentAndChild(website, webFolderChild);
		update(website);
	}

	public Website createInstance(String label) {
		Website website = new Website();
		website.label = label;
		return websiteDao.create(website);
	}
	
	public Website update(Website website) {
		websiteDao.update(website);
		return website;
	}

	public Website get(long orbInternalId) {
		return websiteDao.read(orbInternalId);
	}
}
