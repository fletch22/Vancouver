package com.fletch22.app.designer.appContainer;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class AppContainerTransformer extends DomainTransformer<AppContainer> {
	
	public AppContainer transform(Orb orb) {
		
		AppContainer appContainer = new AppContainer();
		
		this.setBaseAttributes(orb, appContainer);
		appContainer.label = orb.getUserDefinedProperties().get(AppContainer.ATTR_LABEL);
		
		return appContainer;
	}
}
