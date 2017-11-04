package com.fletch22.app.designer.page;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;

@Component
public class PageService extends DomainService<Page, PageChild> {
	
	@Autowired
	PageDao pageDao;

	public Page createInstance(String pageName, String style) {
		Page page = new Page();
		page.pageName = pageName;
		page.style = style;
		save(page);
		return page;
	}
	
	public void save(Page page) {
		pageDao.save(page);
	}

	public Page get(long orbInternalId) {
		return pageDao.read(orbInternalId);
	}
	
	@Override
	public Page createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, Page.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(Page.ATTR_PAGE_NAME), properties.get(Page.ATTR_STYLE));
	}
	
	@Override
	public Page update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Page.ATTRIBUTE_LIST);
		
		Page page = get(id);
		if (properties.containsKey(Page.ATTR_PAGE_NAME)) page.pageName = properties.get(Page.ATTR_PAGE_NAME);
		if (properties.containsKey(Page.ATTR_STYLE)) page.style = properties.get(Page.ATTR_STYLE);
		
		this.save(page);
		
		return page;
	}
}
