package com.fletch22.app.designer.div;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;

@Component
public class DivService extends DomainService<Div, Child> {

	private static final Logger logger = LoggerFactory.getLogger(DivService.class);

	@Autowired
	DivDao divDao;

	public Div createInstance(String style, String ordinal) {
		Div div = new Div();
		div.style = style;
		div.ordinal = ordinal;

		save(div);
		return div;
	}

	public void save(Div div) {
		divDao.save(div);
	}

	public Div get(long orbInternalId) {
		return divDao.read(orbInternalId);
	}

	@Override
	public Div createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, Div.ATTRIBUTE_LIST);

		return createInstance(properties.get(Div.ATTR_STYLE), properties.get(Div.ATTR_ORDINAL));
	} 

	@Override
	public Div update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Div.ATTRIBUTE_LIST);

		Div div = get(id);
		if (properties.containsKey(Div.ATTR_STYLE))
			div.style = properties.get(Div.ATTR_STYLE);
		if (properties.containsKey(Div.ATTR_ORDINAL))
			div.ordinal = properties.get(Div.ATTR_ORDINAL);

		this.save(div);

		return get(id);
	}
}
