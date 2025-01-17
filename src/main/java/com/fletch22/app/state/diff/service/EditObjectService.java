package com.fletch22.app.state.diff.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainServiceBase;
import com.fletch22.app.designer.ServiceFactory;
import com.fletch22.app.designer.ServiceJunction;
import com.fletch22.app.designer.util.DomainUtilDao;
import com.fletch22.app.state.diff.EditedProperty;

@Component
public class EditObjectService {
	
	static Logger logger = LoggerFactory.getLogger(EditObjectService.class);
	
	@Autowired
	ServiceFactory serviceFactory;
	
	@Autowired 
	ServiceJunction serviceJunction;
	
	@Autowired
	DomainUtilDao domainUtilDao;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void process(EditedProperty editedProperty) {
		String typeLabel = domainUtilDao.getTypeLabelFromId(editedProperty.id);
		
		DomainServiceBase domainServiceBase = serviceFactory.getBaseServiceFromTypeLabel(typeLabel);
		
		Map<String, String> map = new HashMap<String, String>();
		logger.info("EditedProp {}", editedProperty.property);
		map.put(editedProperty.property, editedProperty.newValue);
		domainServiceBase.update(editedProperty.id, map);
	}
}
