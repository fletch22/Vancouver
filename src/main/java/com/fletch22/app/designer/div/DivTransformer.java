package com.fletch22.app.designer.div;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class DivTransformer extends DomainTransformer<Div> {
	
	public Div transform(Orb orb) {
		
		Div div = new Div();
		
		this.setBaseAttributes(orb, div);
		div.style = orb.getUserDefinedProperties().get(Div.ATTR_STYLE);
		
		return div;
	}
}
