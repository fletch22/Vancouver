package com.fletch22.app.designer.layoutMinion;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;

@Component
public class LayoutMinionService extends DomainService<LayoutMinion, LayoutMinionChild> {
	
	@Autowired
	LayoutMinionDao layoutDao;

	public LayoutMinion createInstance(String height, String width, String x, String y, String key, String style) {
		LayoutMinion layout = new LayoutMinion();
		layout.height = height;
		layout.width = width;
		layout.x = x;
		layout.y = y;
		layout.key = key;
		layout.style = style;
		
		save(layout);
		return layout;
	}
	
	public void save(LayoutMinion layout) {
		layoutDao.save(layout);
	}

	public LayoutMinion get(long orbInternalId) {
		return layoutDao.read(orbInternalId);
	}
	
	@Override
	public LayoutMinion createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, LayoutMinion.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(LayoutMinion.ATTR_WIDTH), properties.get(LayoutMinion.ATTR_HEIGHT), properties.get(LayoutMinion.ATTR_X), 
				properties.get(LayoutMinion.ATTR_Y), properties.get(LayoutMinion.ATTR_KEY), properties.get(LayoutMinion.ATTR_STYLE));
	}
	
	@Override
	public LayoutMinion update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, LayoutMinion.ATTRIBUTE_LIST);
		
		LayoutMinion layoutMinion = get(id);
		if (properties.containsKey(LayoutMinion.ATTR_WIDTH)) layoutMinion.width = properties.get(LayoutMinion.ATTR_WIDTH);
		if (properties.containsKey(LayoutMinion.ATTR_HEIGHT)) layoutMinion.height = properties.get(LayoutMinion.ATTR_HEIGHT);
		if (properties.containsKey(LayoutMinion.ATTR_X)) layoutMinion.x = properties.get(LayoutMinion.ATTR_X);
		if (properties.containsKey(LayoutMinion.ATTR_Y)) layoutMinion.y = properties.get(LayoutMinion.ATTR_Y);
		if (properties.containsKey(LayoutMinion.ATTR_KEY)) layoutMinion.key = properties.get(LayoutMinion.ATTR_KEY);
		if (properties.containsKey(LayoutMinion.ATTR_STYLE)) layoutMinion.style = properties.get(LayoutMinion.ATTR_STYLE);
		
		this.save(layoutMinion);
		
		return get(id);
	}
}
