package com.fletch22.app.designer.app;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.app.designer.viewmodel.AllModels;
import com.fletch22.orb.Orb;

@Component
public class AppTransformer extends DomainTransformer<App> {
	
	static Logger logger = LoggerFactory.getLogger(AppTransformer.class);

	public App transform(Orb orb) {
		
		App app = new App();
		
		this.setBaseAttributes(orb, app);
		
		logger.debug("setting base attributes for AppTransformer: {}", app.getId());
		
		app.label = orb.getUserDefinedProperties().get(App.ATTR_LABEL);
		
		return app;
	}
	
	public App transform(Map<String, String> attributes) {
		App app = new App();
		
		app.label = attributes.get(App.ATTR_LABEL);
		
		if (attributes.get(App.ATTR_PARENT) != null) {
			app.setParentId(Long.parseLong(attributes.get(App.ATTR_PARENT)));
		}

		if (attributes.get(AllModels.ID) != null) {
			app.setId(Long.parseLong(attributes.get(AllModels.ID)));
		}
		
		return app;
	}
}
