package com.fletch22.app.designer.layout;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.layoutMinion.LayoutMinion;

@Component
public class LayoutService extends DomainService<Layout, LayoutChild> {
	
	@Autowired
	LayoutDao layoutDao;

	public Layout createInstance() {
		Layout layout = new Layout();
		save(layout);
		return layout;
	}
	
	public void save(Layout layout) {
		layoutDao.save(layout);
	}

	public Layout get(long orbInternalId) {
		return layoutDao.read(orbInternalId);
	}
	
	@Override
	public Layout createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, Layout.ATTRIBUTE_LIST);
		
		return createInstance();
	}
	
	@Override
	public Layout update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Layout.ATTRIBUTE_LIST);
		
		Layout layout = get(id);
		this.save(layout);
		
		return get(id);
	}
}
