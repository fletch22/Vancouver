package com.fletch22.app.designer.layoutMinion;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class LayoutMinionTransformer extends DomainTransformer<LayoutMinion> {
	
	public LayoutMinion transform(Orb orb) {
		
		LayoutMinion layout = new LayoutMinion();
		
		this.setBaseAttributes(orb, layout);
		layout.height = orb.getUserDefinedProperties().get(LayoutMinion.ATTR_HEIGHT);
		layout.width = orb.getUserDefinedProperties().get(LayoutMinion.ATTR_WIDTH);
		layout.x = orb.getUserDefinedProperties().get(LayoutMinion.ATTR_X);
		layout.y = orb.getUserDefinedProperties().get(LayoutMinion.ATTR_Y);
		layout.key = orb.getUserDefinedProperties().get(LayoutMinion.ATTR_KEY);
		
		return layout;
	}
}
