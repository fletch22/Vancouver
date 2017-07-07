package com.fletch22.app.designer.ddl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class DropDownListboxDao extends AppDesignerDao<DropDownListbox, DropDownListboxTransformer> {

	Logger logger = LoggerFactory.getLogger(DropDownListboxDao.class);

	@Autowired
	DropDownListboxTransformer dropDownListboxTransformer;

	@Override
	protected void create(DropDownListbox div) {
		OrbType orbType = this.orbTypeManager.getOrbType(DropDownListbox.TYPE_LABEL);
		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(div, orbType);
	}

	@Override
	protected DropDownListboxTransformer getTransformer() {
		return dropDownListboxTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(DropDownListbox ddl, Orb orb) {
		orb.getUserDefinedProperties().put(DropDownListbox.ATTR_STYLE, ddl.style);
		orb.getUserDefinedProperties().put(DropDownListbox.ATTR_ELEMENT_ID, ddl.elementId);
		orb.getUserDefinedProperties().put(DropDownListbox.ATTR_DATASTORE_ID, ddl.dataStoreId);
		orb.getUserDefinedProperties().put(DropDownListbox.ATTR_DATAMODEL_ID, ddl.dataModelId);
		orb.getUserDefinedProperties().put(DropDownListbox.ATTR_VALUE_FIELD_NAME, ddl.dataValueId);
		orb.getUserDefinedProperties().put(DropDownListbox.ATTR_TEXT_FIELD_NAME, ddl.dataTextId);
		
	}
}


