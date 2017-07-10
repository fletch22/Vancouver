package com.fletch22.app.designer.submit;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class ButtonSubmitTransformer extends DomainTransformer<ButtonSubmit> {
	
	public ButtonSubmit transform(Orb orb) {
		
		ButtonSubmit ddl = new ButtonSubmit();
		
		this.setBaseAttributes(orb, ddl);
		ddl.style = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_STYLE);
		ddl.elementId = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_ELEMENT_ID);
		ddl.label = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_LABEL);
		
		return ddl;
	}
}
