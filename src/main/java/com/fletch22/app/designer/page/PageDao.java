package com.fletch22.app.designer.page;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;

@Component
public class PageDao extends AppDesignerDao {

	Logger logger = LoggerFactory.getLogger(PageDao.class);

	@Autowired
	PageTransformer pageTransformer;

	public Page create(Page page) {

		throw new NotImplementedException("Ensure unique child in parent and handle children");
//		OrbType orbType = ensureInstanceUnique(Page.TYPE_LABEL, Page.ATTR_PAGE_NAME, page.pageName);
//		
//		Orb orb = new Orb();
//		orb.setOrbTypeInternalId(orbType.id);
//		orb.getUserDefinedProperties().put(Page.ATTR_PAGE_NAME, page.pageName);
//
//		orb = orbManager.createOrb(orb);
//
//		return pageDaoTransformer.transform(orb);
	}
	
	public Page read(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return pageTransformer.transform(orb);
	}

	public void update(Page page) {

		Orb orbToUpdate = getOrbMustExist(page.getId());

		orbToUpdate.getUserDefinedProperties().put(Page.ATTR_PAGE_NAME, page.pageName);
		
		setOrbChildrenAttribute(page, orbToUpdate);
		
		orbManager.updateOrb(orbToUpdate);
	}
}

