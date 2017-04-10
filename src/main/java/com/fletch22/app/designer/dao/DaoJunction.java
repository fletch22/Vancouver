package com.fletch22.app.designer.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.AppDao;
import com.fletch22.app.designer.appContainer.AppContainerDao;
import com.fletch22.app.designer.dataField.DataFieldDao;
import com.fletch22.app.designer.dataModel.DataModelDao;
import com.fletch22.app.designer.datastore.DatastoreDao;
import com.fletch22.app.designer.ddl.DropDownListboxDao;
import com.fletch22.app.designer.div.DivDao;
import com.fletch22.app.designer.layout.LayoutDao;
import com.fletch22.app.designer.layoutMinion.LayoutMinionDao;
import com.fletch22.app.designer.page.PageDao;
import com.fletch22.app.designer.webFolder.WebFolderDao;
import com.fletch22.app.designer.website.WebsiteDao;

@Component
public class DaoJunction {
	
	@Autowired
	public AppContainerDao appContainerDao;
	
	@Autowired
	public AppDao appDao;
	
	@Autowired
	public WebsiteDao websiteDao;
	
	@Autowired
	public WebFolderDao webFolderDao;
	
	@Autowired
	public PageDao pageDao;
	
	@Autowired
	public LayoutDao layoutDao;
	
	@Autowired
	public LayoutMinionDao layoutMinionDao;
	
	@Autowired
	public DivDao divDao;
	
	@Autowired
	public DropDownListboxDao dropDownListboxDao;
	
	@Autowired
	public DatastoreDao datastoreDao;
	
	@Autowired
	public DataModelDao dataModelDao;
	
	@Autowired
	public DataFieldDao dataFieldDao;
}
