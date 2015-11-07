package com.fletch22.app.designer.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.AppDao;
import com.fletch22.app.designer.appContainer.AppContainerDao;
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
}
