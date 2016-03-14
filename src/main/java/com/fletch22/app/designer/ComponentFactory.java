package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;

@Component
public class ComponentFactory {
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	AppContainerService appContainerService;
	
	@Autowired
	AppService appService;
	
	public Object getInstance(long id)  {
		
		Object object = null;
		Orb orb = orbManager.getOrb(id);
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		String typeLabel = orbType.label;
		
		switch (typeLabel) {
			case AppContainer.TYPE_LABEL:
				AppContainer appContainer = appContainerService.get(id);
				appContainerService.clearAndResolveAllDescendents(appContainer);
				object = appContainer;
				break;
			case App.TYPE_LABEL:
				App app = appService.get(id);
				appService.clearAndResolveAllDescendents(app);
				object = app;
				break;
			default:
				String message = String.format("Could not determine the component from label '%s'.", typeLabel);
				throw new RuntimeException(message);
		}
		
		return object;
	}
}
