package com.fletch22.app.state.diff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.ServiceFactory;
import com.fletch22.app.designer.ServiceJunction;
import com.fletch22.app.designer.dao.BaseDao;
import com.fletch22.app.designer.util.DomainUtilDao;

@Component
public class DeleteService {
	
	Logger logger = LoggerFactory.getLogger(DeleteService.class);
	
	@Autowired
	ServiceFactory serviceFactory;
	
	@Autowired 
	ServiceJunction serviceJunction;
	
	@Autowired
	DomainUtilDao domainUtilDao;
	
	@Autowired 
	BaseDao baseDao;

	public void process(long id) {
		
		logger.info("Attempting to delete the item.");
		
		baseDao.delete(id);
	}
}
