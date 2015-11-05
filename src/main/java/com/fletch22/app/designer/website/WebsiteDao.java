package com.fletch22.app.designer.website;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.query.QueryManager;

@Component
public class WebsiteDao extends AppDesignerDao {

	Logger logger = LoggerFactory.getLogger(WebsiteDao.class);

	@Autowired
	QueryManager queryManager;

	@Autowired
	WebsiteTransformer websiteTransformer;

	public Website create(Website website) {

		throw new NotImplementedException("Ensure unique child in parent and handle children");
//		OrbType orbType = ensureInstanceUnique(Website.TYPE_LABEL, Website.ATTR_LABEL, website.label);
//		
//		Orb orb = new Orb();
//		orb.setOrbTypeInternalId(orbType.id);
//		orb.getUserDefinedProperties().put(Website.ATTR_LABEL, website.label);
//
//		orb = orbManager.createOrb(orb);
//
//		return websiteTransformer.transform(orb);
	}
	
	public Website read(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return websiteTransformer.transform(orb);
	}

	public void update(Website website) {

		Orb orbToUpdate = getOrbMustExist(website.getId());

		orbToUpdate.getUserDefinedProperties().put(Website.ATTR_LABEL, website.label);
		
		setOrbChildrenAttribute(website, orbToUpdate);

		orbManager.updateOrb(orbToUpdate);
	}
}

