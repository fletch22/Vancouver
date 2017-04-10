package com.fletch22.app.designer.div;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;

@Component
public class DivService extends DomainService<Div, Child> {
	
	@Autowired
	DivDao layoutDao;

	public Div createInstance(String style) {
		Div layout = new Div();
		layout.style = style;
		
		save(layout);
		return layout;
	}
	
	public void save(Div layout) {
		layoutDao.save(layout);
	}

	public Div get(long orbInternalId) {
		return layoutDao.read(orbInternalId);
	}
	
	@Override
	public Div createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, Div.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(Div.ATTR_STYLE));
	}
	
	@Override
	public Div update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Div.ATTRIBUTE_LIST);
		
		Div div = get(id);
		if (properties.containsKey(Div.ATTR_STYLE)) div.style = properties.get(Div.ATTR_STYLE);
		
		this.save(div);
		
		return get(id);
	}
}
