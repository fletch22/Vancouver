package com.fletch22.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.google.gson.Gson;

@Component
public class LogActionServiceForTestingOnly {

	@Autowired
	LogActionDao logActionDao;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	LogBundler logBundler;
	
	Gson gson = new Gson();
	
	
}
