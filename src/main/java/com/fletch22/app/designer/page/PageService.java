package com.fletch22.app.designer.page;

import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.website.Website;

@Component
public class PageService extends DomainService<Page, PageChild> {
	
	@Autowired
	PageDao pageDao;

	public Page createInstance(String pageName) {
		Page page = new Page();
		page.pageName = pageName;
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
		throw new NotImplementedException("Not yet finished developing");
	}
	
	@Override
	public Page update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Website.ATTRIBUTE_LIST);
		
		Page page = get(id);
		if (properties.containsKey(Page.ATTR_PAGE_NAME)) page.pageName = properties.get(Page.ATTR_PAGE_NAME);
		
		return page;
	}
}
