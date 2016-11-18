package com.fletch22.app.state.diff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.ServiceFactory;
import com.fletch22.app.designer.ServiceJunction;
import com.fletch22.app.designer.util.DomainUtilDao;
import com.fletch22.app.state.diff.AddedChild;

@Component
public class AddChildService {
	
	static Logger logger = LoggerFactory.getLogger(AddChildService.class);
	
	@Autowired
	ServiceFactory serviceFactory;
	
	@Autowired 
	ServiceJunction serviceJunction;
	
	@Autowired
	DomainUtilDao domainUtilDao;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public long process(AddedChild addedChild) {
		String typeLabel = addedChild.child.typeLabel;
		
		logger.info("Type label: " + typeLabel);

		DomainService domainService = serviceFactory.getServiceFromTypeLabel(typeLabel);
		Child child = domainService.createInstance(addedChild.child.props);
		
		logger.info("Par ID: " + addedChild.parentId);
		
		String typeLabelParent = domainUtilDao.getTypeLabelFromId(addedChild.parentId);
		domainService = serviceFactory.getServiceFromTypeLabel(typeLabelParent);
		domainService.addToParent(domainService.get(addedChild.parentId), child); 
		
		return child.getId();
	}
}
