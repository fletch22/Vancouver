package com.fletch22.app.designer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.util.DomainUtilDao;
import com.fletch22.app.designer.viewmodel.AllModels;
import com.fletch22.web.controllers.TransformerDocks;

@Component
public class ComponentSaveFromMapService {
	
	@Autowired
	TransformerDocks transformerDocks;
	
	@Autowired
	ServiceJunction serviceJunction;
	
	@Autowired
	DomainUtilDao domainServiceUtil;
	
	@Autowired
	ServiceFactory serviceFactory;

	public OrbBasedComponent save(Map<String, String> map) {
		String typeLabel = map.get(AllModels.TYPE_LABEL);

		OrbBasedComponent savedObject = null;
		switch (typeLabel) {
			case App.TYPE_LABEL:
				App app = transformerDocks.appTransformer.transform(map);
				serviceJunction.appService.save(app);
				
				long parentId = app.getParentId();
				String typeLabelParent = domainServiceUtil.getTypeLabelFromId(parentId);
				DomainService domainService = (AppContainerService) serviceFactory.getServiceFromTypeLabel(typeLabelParent);
				domainService.addToParent(domainService.get(parentId), app);
				
				savedObject = app;
				break;
			default:
				String message = String.format("Could not process type label '%s'. No processor.", typeLabel);
				throw new RuntimeException(message);
		}
		return savedObject;
	}

	public void delete(long id) {
		return ;
	}
	
}
