package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.layout.Layout;
import com.fletch22.app.designer.layoutMinion.LayoutMinion;
import com.fletch22.app.designer.page.Page;
import com.fletch22.app.designer.webFolder.WebFolder;
import com.fletch22.app.designer.website.Website;

@Component
public class ServiceFactory {

	@Autowired
	ServiceJunction serviceJunction;
	
	@SuppressWarnings("rawtypes")
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
			case WebFolder.TYPE_LABEL:
				domainService = serviceJunction.webFolderService;
				break;
			case Page.TYPE_LABEL:
				domainService = serviceJunction.pageService;
				break;
			case Layout.TYPE_LABEL:
				domainService = serviceJunction.layoutService;
				break;
			case LayoutMinion.TYPE_LABEL:
				domainService = serviceJunction.layoutMinionService;
				break;
			default:
				throw new RuntimeException("Could not determine the type of service from the label '" + typeLabel + "'");
		}
		return domainService;
	}
}
