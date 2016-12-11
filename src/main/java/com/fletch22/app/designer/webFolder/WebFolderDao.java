package com.fletch22.app.designer.webFolder;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;

@Component
public class WebFolderDao extends AppDesignerDao<WebFolder, WebFolderTransformer> {

	Logger logger = LoggerFactory.getLogger(WebFolderDao.class);

	@Autowired
	WebFolderTransformer webFolderTransformer;

	public void create(WebFolder webFolder) {
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
	
	@Override
	protected WebFolderTransformer getTransformer() {
		return webFolderTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(WebFolder webFolder, Orb orb) {
		orb.getUserDefinedProperties().put(WebFolder.ATTR_LABEL, webFolder.label);
	}
}

