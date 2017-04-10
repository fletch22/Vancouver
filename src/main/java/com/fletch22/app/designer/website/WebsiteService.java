package com.fletch22.app.designer.website;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.webFolder.WebFolderChild;

@Component
public class WebsiteService extends DomainService<Website, WebFolderChild> {
	
	@Autowired
	WebsiteDao websiteDao;

	public void addToParent(Website website, WebFolderChild webFolderChild) {
		connectParentAndChild(website, webFolderChild);
		save(website);
	}

	public Website createInstance(String label) {
		Website website = new Website();
		website.label = label;
		save(website);
		return website;
	}
	
	public void save(Website website) {
		websiteDao.save(website);
	}

	public Website get(long orbInternalId) {
		return websiteDao.read(orbInternalId);
	}
		
	@Override
	public Website createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, Website.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(Website.ATTR_LABEL));
	}
	
	@Override
	public Website update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Website.ATTRIBUTE_LIST);
		
		Website website = get(id);
		if (properties.containsKey(Website.ATTR_LABEL)) website.label = properties.get(Website.ATTR_LABEL);
		
		this.save(website);
		
		return website;
	}
}
