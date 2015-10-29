package com.fletch22.app.designer.appContainer;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class AppContainerTransformer extends DomainTransformer {
	
	public AppContainer transformToFlyweight(Orb orb) {
		return transform(orb, false);
	}
	
	public AppContainer transform(Orb orb) {
		return transform(orb, true);
	}

	private AppContainer transform(Orb orb, boolean isResolveAllChildren) {
		
		AppContainer appContainer = new AppContainer();
		
		this.setBaseAttributes(orb, appContainer);
		appContainer.setLabel(orb.getUserDefinedProperties().get(AppContainer.ATTR_LABEL));
		
		if (isResolveAllChildren) {
			String references = orb.getUserDefinedProperties().get(AppContainer.ATTR_APPS);
			if (references != null) {
				resolveChildren(appContainer, references, isResolveAllChildren);
			}
		}
		
		return appContainer;
	}
}
