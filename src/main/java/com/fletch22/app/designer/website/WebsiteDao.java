package com.fletch22.app.designer.website;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class WebsiteDao extends AppDesignerDao<Website, WebsiteTransformer> {

	Logger logger = LoggerFactory.getLogger(WebsiteDao.class);

	@Autowired
	WebsiteTransformer websiteTransformer;

	@Override
	public void create(Website website) {

		OrbType orbType = ensureInstanceUnique(Website.TYPE_LABEL, Website.ATTR_LABEL, website.label);
		
		create(website, orbType);
	}
	
	@Override
	protected WebsiteTransformer getTransformer() {
		return websiteTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(Website website, Orb orb) {
		orb.getUserDefinedProperties().put(Website.ATTR_LABEL, website.label);
	}
}

