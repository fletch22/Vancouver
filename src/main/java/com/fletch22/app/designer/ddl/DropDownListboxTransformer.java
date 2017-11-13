package com.fletch22.app.designer.ddl;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.app.designer.div.Div;
import com.fletch22.orb.Orb;

@Component
public class DropDownListboxTransformer extends DomainTransformer<DropDownListbox> {
	
	public DropDownListbox transform(Orb orb) {
		
		DropDownListbox ddl = new DropDownListbox();
		
		this.setBaseAttributes(orb, ddl);
		ddl.style = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_STYLE);
		ddl.elementId = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_ELEMENT_ID);
		ddl.dataStoreId = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_DATASTORE_ID);
		ddl.dataModelId = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_DATAMODEL_ID);
		ddl.dataValueId = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_VALUE_FIELD_NAME);
		ddl.dataTextId = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_TEXT_FIELD_NAME);
		ddl.ordinal = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_ORDINAL);
		
		return ddl;
	}
}
