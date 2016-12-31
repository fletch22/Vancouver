package com.fletch22.app.designer.webFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class WebFolderDao extends AppDesignerDao<WebFolder, WebFolderTransformer> {

	Logger logger = LoggerFactory.getLogger(WebFolderDao.class);

	@Autowired
	WebFolderTransformer webFolderTransformer;

	public void create(WebFolder webFolder) {
		OrbType orbType = this.orbTypeManager.getOrbType(WebFolder.TYPE_LABEL);
		
		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
				
		create(webFolder, orbType);
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

