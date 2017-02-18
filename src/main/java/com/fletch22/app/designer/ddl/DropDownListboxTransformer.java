package com.fletch22.app.designer.ddl;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class DropDownListboxTransformer extends DomainTransformer<DropDownListbox> {
	
	public DropDownListbox transform(Orb orb) {
		
		DropDownListbox ddl = new DropDownListbox();
		
		this.setBaseAttributes(orb, ddl);
		ddl.style = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_STYLE);
		ddl.name = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_NAME);
		ddl.dataSourceName = orb.getUserDefinedProperties().get(DropDownListbox.ATTR_DATA_SOURCE_NAME);
		
		return ddl;
	}
}
