package com.fletch22.app.designer.layout;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class LayoutTransformer extends DomainTransformer<Layout> {
	
	public Layout transform(Orb orb) {
		
		Layout layout = new Layout();
		
		this.setBaseAttributes(orb, layout);
		
		return layout;
	}
}
