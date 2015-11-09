package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.page.Page;
import com.fletch22.app.designer.page.body.Body;
import com.fletch22.app.designer.page.div.Div;
import com.fletch22.app.designer.page.form.Form;
import com.fletch22.app.designer.page.head.Head;
import com.fletch22.app.designer.webFolder.WebFolder;
import com.fletch22.app.designer.website.Website;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.modules.system.OrbSystemModule;
import com.fletch22.orb.query.CriteriaManager;

@Component
public class AppDesignerModule implements OrbSystemModule {
	
	@Autowired
	OrbManager orbManager;

	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	CriteriaManager queryManager;
	
	@Autowired
	AppContainerService appContainerService;
	
	@Autowired
	AppService appService;
	
	@Override
	public void initialize() {
		
		createTypes();
		createInstances();
	}

	private void createInstances() {
		AppContainer appContainer = appContainerService.createInstance("HelloWorldAppContainer");
		
		App app = appService.createInstance("HelloWorldApp");
		appContainerService.addToParent(appContainer, app);
	}

	private void createTypes() {
		long orbTypeInternalId = orbTypeManager.createOrbType(AppContainer.TYPE_LABEL, AppContainer.ATTRIBUTE_LIST); 
		primeQueryIndex(orbTypeInternalId, AppContainer.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(App.TYPE_LABEL, App.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, App.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(Website.TYPE_LABEL, Website.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, Website.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(WebFolder.TYPE_LABEL, WebFolder.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, WebFolder.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(Page.TYPE_LABEL, Page.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, Page.ATTR_PAGE_NAME);
		
		orbTypeInternalId = orbTypeManager.createOrbType(Head.TYPE_LABEL, Head.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, Head.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(Body.TYPE_LABEL, Body.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, Body.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(Div.TYPE_LABEL, Div.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, Div.ATTR_LABEL);
		
		orbTypeInternalId = orbTypeManager.createOrbType(Form.TYPE_LABEL, Form.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, Form.ATTR_LABEL);
	}
	
	private void primeQueryIndex(long orbTypeInternalId, String attributeName) {
		Orb orb = orbManager.createOrb(orbTypeInternalId);
		orbManager.deleteOrb(orb.getOrbInternalId(), false);
		this.queryManager.findByAttribute(orbTypeInternalId, attributeName, "foo");
	}
}
