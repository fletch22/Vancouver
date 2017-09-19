package com.fletch22.app.designer.submit;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;

@Component
public class ButtonSubmitService extends DomainService<ButtonSubmit, Child> {
	
	@Autowired
	ButtonSubmitDao buttonSubmitDao;

	public ButtonSubmit createInstance(String style, String elementId, String label) {
		ButtonSubmit buttonSubmit = new ButtonSubmit();
		buttonSubmit.style = style;
		buttonSubmit.elementId = elementId;
		buttonSubmit.label = label;
		
		save(buttonSubmit);
		return buttonSubmit;
	}
	
	public void save(ButtonSubmit layout) {
		buttonSubmitDao.save(layout);
	}

	public ButtonSubmit get(long orbInternalId) {
		return buttonSubmitDao.read(orbInternalId);
	}
	
	@Override
	public ButtonSubmit createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, ButtonSubmit.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(ButtonSubmit.ATTR_STYLE),
				properties.get(ButtonSubmit.ATTR_ELEMENT_ID),
				properties.get(ButtonSubmit.ATTR_LABEL));
	}
	
	@Override
	public ButtonSubmit update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, ButtonSubmit.ATTRIBUTE_LIST);
		
		ButtonSubmit ddl = get(id);
		if (properties.containsKey(ButtonSubmit.ATTR_STYLE)) ddl.style = properties.get(ButtonSubmit.ATTR_STYLE);
		if (properties.containsKey(ButtonSubmit.ATTR_ELEMENT_ID)) ddl.elementId = properties.get(ButtonSubmit.ATTR_ELEMENT_ID);
		if (properties.containsKey(ButtonSubmit.ATTR_LABEL)) ddl.elementId = properties.get(ButtonSubmit.ATTR_LABEL);
		
		this.save(ddl);
		
		return get(id);
	}
}
