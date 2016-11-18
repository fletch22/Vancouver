package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.website.Website;

@Component
public class ServiceFactory {

	@Autowired
	ServiceJunction serviceJunction;
	
	public DomainService getServiceFromTypeLabel(String typeLabel) {
		
		DomainService domainService = null;
		switch (typeLabel) {
			case AppContainer.TYPE_LABEL:
				domainService = serviceJunction.appContainerService;
				break;
			case App.TYPE_LABEL:
				domainService = serviceJunction.appService;
				break;
			case Website.TYPE_LABEL:
				domainService = serviceJunction.websiteService;
				break;
			default:
				throw new RuntimeException("Could not determine the type of service from the label '" + typeLabel + "'");
		}
		return domainService;
	}
}
