package com.fletch22.app.state;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;
import com.fletch22.web.controllers.ComponentController.StatePackage;

@Component
public class FrontEndStateService {
	
	Logger logger = LoggerFactory.getLogger(FrontEndStateService.class);
	
	@Autowired
	FrontEndStateDao frontEndStateDao;

	@Transactional
	public void save(String state) {
		frontEndStateDao.save(state);
	}
	
	@Transactional
	public void save(List<StatePackage> statePackageList) {
		for (StatePackage statePackage : statePackageList) {
			logger.info(statePackage.state);
			save(statePackage.state);
		}
	}

	public StateIndexInfo getHistorical(int index) {
		return frontEndStateDao.getHistorical(index);
	}
}
