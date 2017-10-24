package com.fletch22.app.designer.submit;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class ButtonSubmitTransformer extends DomainTransformer<ButtonSubmit> {
	
	public ButtonSubmit transform(Orb orb) {
		
		ButtonSubmit buttonSubmit = new ButtonSubmit();
		
		this.setBaseAttributes(orb, buttonSubmit);
		buttonSubmit.style = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_STYLE);
		buttonSubmit.elementId = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_ELEMENT_ID);
		buttonSubmit.label = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_LABEL);
		buttonSubmit.ordinal = orb.getUserDefinedProperties().get(ButtonSubmit.ATTR_ORDINAL);
		
		return buttonSubmit;
	}
}
