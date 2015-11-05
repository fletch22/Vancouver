package com.fletch22.app.designer.webFolder;

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
public class WebFolderDao extends AppDesignerDao {

	Logger logger = LoggerFactory.getLogger(WebFolderDao.class);

	@Autowired
	QueryManager queryManager;

	@Autowired
	WebFolderTransformer webFolderTransformer;

	public WebFolder create(WebFolder webFolder) {

		throw new NotImplementedException("Ensure unique child in parent and handle children");
//		OrbType orbType = ensureInstanceUnique(WebSection.TYPE_LABEL, WebSection.ATTR_LABEL, webFolder.label);
//		
//		Orb orb = new Orb();
//		orb.setOrbTypeInternalId(orbType.id);
//		orb.getUserDefinedProperties().put(WebSection.ATTR_LABEL, webFolder.label);
//
//		orb = orbManager.createOrb(orb);
//
//		return webFolderTransformer.transform(orb);
	}
	
	public WebFolder read(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return webFolderTransformer.transform(orb);
	}

	public void update(WebFolder webFolder) {

		Orb orbToUpdate = getOrbMustExist(webFolder.getId());

		orbToUpdate.getUserDefinedProperties().put(WebFolder.ATTR_LABEL, webFolder.label);
		
		setOrbChildrenAttribute(webFolder, orbToUpdate);
		
		orbManager.updateOrb(orbToUpdate);
	}
}

