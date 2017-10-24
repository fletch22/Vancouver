package com.fletch22.app.state.diff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.DomainServiceBase;
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
		
		DomainServiceBase domainServiceBase = serviceFactory.getBaseServiceFromTypeLabel(typeLabel);
		Child child = domainServiceBase.createInstance(addedChild.child.props);
		
		String typeLabelParent = domainUtilDao.getTypeLabelFromId(addedChild.parentId);
		DomainService domainService = serviceFactory.getServiceFromTypeLabel(typeLabelParent);
		domainService.addToParent(domainService.get(addedChild.parentId), child, child.getOrdinalAsNumber()); 
		
		return child.getId();
	}
}
