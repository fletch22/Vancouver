package com.fletch22.app.designer.page;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class PageTransformer extends DomainTransformer<Page> {
	
	public Page transform(Orb orb) {
		
		Page page = new Page();
		
		this.setBaseAttributes(orb, page);
		page.pageName = orb.getUserDefinedProperties().get(Page.ATTR_PAGE_NAME);
		
		return page;
	}
}
