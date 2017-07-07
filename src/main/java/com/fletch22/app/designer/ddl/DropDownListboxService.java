package com.fletch22.app.designer.ddl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainServiceBase;

@Component
public class DropDownListboxService extends DomainServiceBase<DropDownListbox> {
	
	@Autowired
	DropDownListboxDao dropDownListboxDao;

	public DropDownListbox createInstance(String style, String name, String dataStoreId, String dataModelId, String dataValueId, String dataTextId) {
		DropDownListbox dropDownListbox = new DropDownListbox();
		dropDownListbox.style = style;
		dropDownListbox.elementId = name;
		dropDownListbox.dataStoreId = dataStoreId;
		dropDownListbox.dataModelId = dataModelId;
		dropDownListbox.dataValueId = dataValueId;
		dropDownListbox.dataTextId = dataTextId;
		
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
				properties.get(DropDownListbox.ATTR_ELEMENT_ID),
				properties.get(DropDownListbox.ATTR_DATASTORE_ID),
				properties.get(DropDownListbox.ATTR_DATAMODEL_ID),
				properties.get(DropDownListbox.ATTR_VALUE_FIELD_NAME),
				properties.get(DropDownListbox.ATTR_TEXT_FIELD_NAME));
	}
	
	@Override
	public DropDownListbox update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, DropDownListbox.ATTRIBUTE_LIST);
		
		DropDownListbox ddl = get(id);
		if (properties.containsKey(DropDownListbox.ATTR_STYLE)) ddl.style = properties.get(DropDownListbox.ATTR_STYLE);
		if (properties.containsKey(DropDownListbox.ATTR_ELEMENT_ID)) ddl.elementId = properties.get(DropDownListbox.ATTR_ELEMENT_ID);
		if (properties.containsKey(DropDownListbox.ATTR_DATASTORE_ID)) ddl.dataStoreId = properties.get(DropDownListbox.ATTR_DATASTORE_ID);
		
		this.save(ddl);
		
		return get(id);
	}
}
