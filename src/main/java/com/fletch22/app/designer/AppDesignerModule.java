package com.fletch22.app.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.AppModule;
import com.fletch22.app.AppModuleImpl;
import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.ddl.DropDownListbox;
import com.fletch22.app.designer.ddl.DropDownListboxService;
import com.fletch22.app.designer.div.Div;
import com.fletch22.app.designer.layout.Layout;
import com.fletch22.app.designer.layoutMinion.LayoutMinion;
import com.fletch22.app.designer.page.Page;
import com.fletch22.app.designer.page.PageService;
import com.fletch22.app.designer.page.body.Body;
import com.fletch22.app.designer.page.form.Form;
import com.fletch22.app.designer.page.head.Head;
import com.fletch22.app.designer.webFolder.WebFolder;
import com.fletch22.app.designer.website.Website;
import com.fletch22.app.designer.website.WebsiteService;
import com.fletch22.app.state.FrontEndState;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.modules.system.OrbSystemModule;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaStandard;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.query.sort.SortInfo.SortDirection;

@Component
public class AppDesignerModule implements OrbSystemModule {

	Logger logger = LoggerFactory.getLogger(AppDesignerModule.class);

	public static final String DEFAULT_APP_CONTAINER_NAME = "DefaultAppContainer";

	@Autowired
	OrbManager orbManager;

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	QueryManager queryManager;

	@Autowired
	AppContainerService appContainerService;

	@Autowired
	AppService appService;
	
	@Autowired
	WebsiteService websiteService;
	
	@Autowired
	PageService pageService;
	
	@Autowired
	DropDownListboxService dropDownListboxService;

	@Autowired
	ComponentConstrainer componentConstrainer;

	@Override
	public void initialize() {
		createTypes();
		createInstances();
	}

	private void createInstances() {
		logger.debug("Creating instances....");

		AppContainer appContainer = appContainerService.createInstance(DEFAULT_APP_CONTAINER_NAME);

		App app = appService.createInstance("HelloWorldApp");
		appContainerService.addToParent(appContainer, app);
		
		Website website = websiteService.createInstance("website1");
		appService.addToParent(app, website);
		
		Page page = pageService.createInstance("page1");
		websiteService.addToParent(website, page);
	}

	private void createTypes() {
		long orbTypeInternalId = orbTypeManager.createOrbType(AppContainer.TYPE_LABEL, AppContainer.ATTRIBUTE_LIST);
		primeQueryIndex(orbTypeInternalId, AppContainer.ATTR_LABEL);
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, AppContainer.ATTR_LABEL);

		orbTypeInternalId = orbTypeManager.createOrbType(App.TYPE_LABEL, App.ATTRIBUTE_LIST);
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, App.ATTR_LABEL);
		primeQueryIndex(orbTypeInternalId, App.ATTR_LABEL);

		orbTypeInternalId = orbTypeManager.createOrbType(Website.TYPE_LABEL, Website.ATTRIBUTE_LIST);
		String[] websiteCompositeUniqueContraint = { Website.ATTR_LABEL, Website.ATTR_PARENT };
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, websiteCompositeUniqueContraint);
		primeQueryIndex(orbTypeInternalId, Website.ATTR_LABEL);

		orbTypeInternalId = orbTypeManager.createOrbType(WebFolder.TYPE_LABEL, WebFolder.ATTRIBUTE_LIST);
		String[] webfolderCompositeUniqueContraint = { WebFolder.ATTR_LABEL, WebFolder.ATTR_PARENT };
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, webfolderCompositeUniqueContraint);
		primeQueryIndex(orbTypeInternalId, WebFolder.ATTR_LABEL);

		orbTypeInternalId = orbTypeManager.createOrbType(Page.TYPE_LABEL, Page.ATTRIBUTE_LIST);
		String[] pageCompositeUniqueContraint = { Page.ATTR_PAGE_NAME, Page.ATTR_PARENT };
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, pageCompositeUniqueContraint);
		primeQueryIndex(orbTypeInternalId, Page.ATTR_PAGE_NAME);

		orbTypeInternalId = orbTypeManager.createOrbType(Head.TYPE_LABEL, Head.ATTRIBUTE_LIST);
		String[] headCompositeUniqueContraint = { Head.ATTR_LABEL, Head.ATTR_PARENT };
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, headCompositeUniqueContraint);
		primeQueryIndex(orbTypeInternalId, Head.ATTR_LABEL);

		orbTypeInternalId = orbTypeManager.createOrbType(Body.TYPE_LABEL, Body.ATTRIBUTE_LIST);
		String[] bodyCompositeUniqueContraint = { Body.ATTR_LABEL, Body.ATTR_PARENT };
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, bodyCompositeUniqueContraint);
		primeQueryIndex(orbTypeInternalId, Body.ATTR_LABEL);
		
		orbTypeManager.createOrbType(Layout.TYPE_LABEL, Layout.ATTRIBUTE_LIST);
		
		orbTypeManager.createOrbType(LayoutMinion.TYPE_LABEL, LayoutMinion.ATTRIBUTE_LIST);

		orbTypeInternalId = orbTypeManager.createOrbType(Div.TYPE_LABEL, Div.ATTRIBUTE_LIST);
		
		orbTypeInternalId = orbTypeManager.createOrbType(DropDownListbox.TYPE_LABEL, DropDownListbox.ATTRIBUTE_LIST);

		orbTypeInternalId = orbTypeManager.createOrbType(Form.TYPE_LABEL, Form.ATTRIBUTE_LIST);
		componentConstrainer.addNotAmongstUniqueConstraintOnField(orbTypeInternalId, Form.ATTR_LABEL);
		primeQueryIndex(orbTypeInternalId, Form.ATTR_LABEL);
		
		createType(AppModuleImpl.FrontEndState);
	}

	public void createType(AppModule appModule) {
		long orbTypeInternalId = orbTypeManager.createOrbType(appModule.getTypeLabel(), appModule.getAttributes());

		String randomAttributeName = appModule.getAttributes().iterator().next();
		primeQueryIndex(orbTypeInternalId, randomAttributeName);

		createFrontEndStateQueryGetStates();
	}

	private void createFrontEndStateQueryGetStates() {
		OrbType orbType = orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
		Criteria criteria = new CriteriaStandard(orbType.id, FrontEndState.QUERY_GET_STATES);

		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo();
		criteriaSortInfo.sortDirection = SortDirection.DESC;
		criteriaSortInfo.sortAttributeName = FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID;

		criteria.setSortOrder(criteriaSortInfo);

		this.queryManager.addToCollection(criteria);
	}

	private void primeQueryIndex(long orbTypeInternalId, String attributeName) {
		Orb orb = orbManager.createOrb(orbTypeInternalId);
		orbManager.deleteOrb(orb.getOrbInternalId(), false);
		this.queryManager.findByAttribute(orbTypeInternalId, attributeName, "foo");
	}
}
