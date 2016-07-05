package com.fletch22.app.state.diff.service;

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
	
	@Autowired
	ServiceFactory serviceFactory;
	
	@Autowired 
	ServiceJunction serviceJunction;
	
	@Autowired
	DomainUtilDao domainUtilDao;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public long process(AddedChild addedChild) {
		String typeLabel = addedChild.child.typeLabel;

		DomainService domainService = serviceFactory.getServiceFromTypeLabel(typeLabel);
		Child child = domainService.createInstance(addedChild.child.props);
		
		String typeLabelParent = domainUtilDao.getTypeLabelFromId(addedChild.parentId);
		domainService = serviceFactory.getServiceFromTypeLabel(typeLabelParent);
		domainService.addToParent(domainService.get(addedChild.parentId), child);
		
		return child.getId();
	}
}
