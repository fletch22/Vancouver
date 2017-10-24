package com.fletch22.app.designer.submit;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainServiceBase;

@Component
public class ButtonSubmitService extends DomainServiceBase<ButtonSubmit> {
	
	private static Logger logger = LoggerFactory.getLogger(ButtonSubmitService.class);

	@Autowired
	ButtonSubmitDao buttonSubmitDao;

	public ButtonSubmit createInstance(String style, String elementId, String label, String ordinal) {
		ButtonSubmit buttonSubmit = new ButtonSubmit();
		buttonSubmit.style = style;
		buttonSubmit.elementId = elementId;
		buttonSubmit.label = label;
		logger.info("BS Ordinal: {}", ordinal);
		buttonSubmit.ordinal = ordinal;

		save(buttonSubmit);
		return buttonSubmit;
	}

	public void save(ButtonSubmit buttonSubmit) {
		buttonSubmitDao.save(buttonSubmit);
	}

	public ButtonSubmit get(long orbInternalId) {
		return buttonSubmitDao.read(orbInternalId);
	}

	@Override
	public ButtonSubmit createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, ButtonSubmit.ATTRIBUTE_LIST);

		return createInstance(properties.get(ButtonSubmit.ATTR_STYLE), properties.get(ButtonSubmit.ATTR_ELEMENT_ID), properties.get(ButtonSubmit.ATTR_LABEL), properties.get(ButtonSubmit.ATTR_ORDINAL));
	}

	@Override
	public ButtonSubmit update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, ButtonSubmit.ATTRIBUTE_LIST);
		
		logger.info("Properties length: {}", properties.size());

		ButtonSubmit buttonSubmit = get(id);
		if (properties.containsKey(ButtonSubmit.ATTR_STYLE))
			buttonSubmit.style = properties.get(ButtonSubmit.ATTR_STYLE);
		if (properties.containsKey(ButtonSubmit.ATTR_ELEMENT_ID))
			buttonSubmit.elementId = properties.get(ButtonSubmit.ATTR_ELEMENT_ID);
		if (properties.containsKey(ButtonSubmit.ATTR_LABEL))
			buttonSubmit.label = properties.get(ButtonSubmit.ATTR_LABEL);
		if (properties.containsKey(ButtonSubmit.ATTR_ORDINAL)) {
			buttonSubmit.ordinal = properties.get(ButtonSubmit.ATTR_ORDINAL);
		}

		this.save(buttonSubmit);

		return get(id);
	}
}
