package com.fletch22.app.designer.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class PageDao extends AppDesignerDao<Page, PageTransformer> {

	Logger logger = LoggerFactory.getLogger(PageDao.class);

	@Autowired
	PageTransformer pageTransformer;

	@Override
	protected void create(Page page) {
		OrbType orbType = this.orbTypeManager.getOrbType(Page.TYPE_LABEL);

		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(page, orbType);
	}

	@Override
	protected PageTransformer getTransformer() {
		return pageTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(Page page, Orb orb) {
		orb.getUserDefinedProperties().put(Page.ATTR_PAGE_NAME, page.pageName);
		orb.getUserDefinedProperties().put(Page.ATTR_STYLE, page.style);
	}
}

