package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.dataField.DataField;
import com.fletch22.app.designer.dataModel.DataModel;
import com.fletch22.app.designer.dataUniverse.DataUniverse;
import com.fletch22.app.designer.datastore.Datastore;
import com.fletch22.app.designer.ddl.DropDownListbox;
import com.fletch22.app.designer.div.Div;
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
		DomainService domainService = getDomainServiceForParents(typeLabel);
		if (domainService == null) {
			throw new RuntimeException("Could not determine the type of service from the label '" + typeLabel + "'");
		}
		return domainService;
	}
	
	DomainService getDomainServiceForParents(String parentTypeLabel) {
		DomainService domainService = null;
		switch (parentTypeLabel) {
			case AppContainer.TYPE_LABEL:
				domainService = serviceJunction.appContainerService;
				break;
			case App.TYPE_LABEL:
				domainService = serviceJunction.appService;
				break;
			case DataUniverse.TYPE_LABEL:
				domainService = serviceJunction.dataUniverseService;
				break;
			case Datastore.TYPE_LABEL:
				domainService = serviceJunction.datastoreService;
				break;
			case DataModel.TYPE_LABEL:
				domainService = serviceJunction.dataModelService;
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
			case Div.TYPE_LABEL:
				domainService = serviceJunction.divService;
				break;
			default:
				// Do nothing
		}
		return domainService;
	}
	
	@SuppressWarnings("rawtypes")
	public DomainServiceBase getBaseServiceFromTypeLabel(String typeLabel) {
		DomainServiceBase domainServiceBase = getDomainServiceForParents(typeLabel);
		if (domainServiceBase == null) {
			switch (typeLabel) {
				case DropDownListbox.TYPE_LABEL:
					domainServiceBase = serviceJunction.dropDownListboxService;
					break;
				case DataField.TYPE_LABEL:
					domainServiceBase = serviceJunction.dataFieldService;
					break;
				default:
					throw new RuntimeException("Could not determine the type of service from the label '" + typeLabel + "'");
			}
		}
		return domainServiceBase;
	}
}
