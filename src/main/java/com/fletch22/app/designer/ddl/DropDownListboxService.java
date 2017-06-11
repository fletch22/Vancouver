package com.fletch22.app.designer.ddl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainServiceBase;

@Component
public class DropDownListboxService extends DomainServiceBase<DropDownListbox> {
	
	@Autowired
	DropDownListboxDao dropDownListboxDao;

	public DropDownListbox createInstance(String style, String label, String dataSourceName) {
		DropDownListbox dropDownListbox = new DropDownListbox();
		dropDownListbox.style = style;
		dropDownListbox.name = label;
		dropDownListbox.dataSourceId = dataSourceName;
		
		save(dropDownListbox);
		return dropDownListbox;
	}
	
	public void save(DropDownListbox layout) {
		dropDownListboxDao.save(layout);
	}

	public DropDownListbox get(long orbInternalId) {
		return dropDownListboxDao.read(orbInternalId);
	}
	
	@Override
	public DropDownListbox createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, DropDownListbox.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(DropDownListbox.ATTR_STYLE),
				properties.get(DropDownListbox.ATTR_NAME),
				properties.get(DropDownListbox.ATTR_DATA_SOURCE_ID));
	}
	
	@Override
	public DropDownListbox update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, DropDownListbox.ATTRIBUTE_LIST);
		
		DropDownListbox ddl = get(id);
		if (properties.containsKey(DropDownListbox.ATTR_STYLE)) ddl.style = properties.get(DropDownListbox.ATTR_STYLE);
		if (properties.containsKey(DropDownListbox.ATTR_NAME)) ddl.name = properties.get(DropDownListbox.ATTR_NAME);
		if (properties.containsKey(DropDownListbox.ATTR_DATA_SOURCE_ID)) ddl.dataSourceId = properties.get(DropDownListbox.ATTR_DATA_SOURCE_ID);
		
		this.save(ddl);
		
		return get(id);
	}
}
