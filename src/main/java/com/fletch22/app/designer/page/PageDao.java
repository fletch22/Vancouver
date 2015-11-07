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
		
		OrbType orbType = ensureInstanceUnique(Page.TYPE_LABEL, Page.ATTR_PAGE_NAME, page.pageName);

		create(page, orbType);
	}

	@Override
	protected PageTransformer getTransformer() {
		return pageTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(Page page, Orb orb) {
		orb.getUserDefinedProperties().put(Page.ATTR_PAGE_NAME, page.pageName);
	}
}

