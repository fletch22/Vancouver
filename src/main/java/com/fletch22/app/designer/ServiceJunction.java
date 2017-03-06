package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DataField.DataFieldService;
import com.fletch22.app.designer.DataModel.DataModelService;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.datastore.DatastoreService;
import com.fletch22.app.designer.ddl.DropDownListboxService;
import com.fletch22.app.designer.div.DivService;
import com.fletch22.app.designer.layout.LayoutService;
import com.fletch22.app.designer.layoutMinion.LayoutMinionService;
import com.fletch22.app.designer.page.PageService;
import com.fletch22.app.designer.webFolder.WebFolderService;
import com.fletch22.app.designer.website.WebsiteService;

@Component
public class ServiceJunction {

	@Autowired
	public AppContainerService appContainerService;
	
	@Autowired
	public AppService appService;
	
	@Autowired
	public WebsiteService websiteService;
	
	@Autowired
	public WebFolderService webFolderService;
	
	@Autowired
	public PageService pageService;
	
	@Autowired
	public LayoutService layoutService;
	
	@Autowired
	public LayoutMinionService layoutMinionService;
	
	@Autowired
	public DivService divService;
	
	@Autowired
	public DropDownListboxService dropDownListboxService;
	
	@Autowired
	public DatastoreService datastoreService;
	
	@Autowired
	public DataModelService dataModelService;
	
	@Autowired
	public DataFieldService dataFieldService;
}
