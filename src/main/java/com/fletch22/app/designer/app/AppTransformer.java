package com.fletch22.app.designer.app;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class AppTransformer extends DomainTransformer {

	public App transform(Orb orb) {
		
		App app = new App();
		
		this.setBaseAttributes(orb, app);
		app.setLabel(orb.getUserDefinedProperties().get(App.ATTR_LABEL));
		
		return app;
	}
}
