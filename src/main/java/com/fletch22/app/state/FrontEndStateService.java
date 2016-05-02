package com.fletch22.app.state;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.state.diff.service.JsonDiffProcessorService;
import com.fletch22.web.controllers.ComponentController.StatePackage;

@Component
public class FrontEndStateService {
	
	Logger logger = LoggerFactory.getLogger(FrontEndStateService.class);
	
	@Autowired
	FrontEndStateDao frontEndStateDao;
	
	@Autowired
	JsonDiffProcessorService jsonDiffProcessorService;
	
	@Autowired
	AppContainerService appContainerService;

	@Transactional
	public void save(String state) {
		frontEndStateDao.save(state);
	}
	
	@Transactional
	public void save(List<StatePackage> statePackageList) {
		for (StatePackage statePackage : statePackageList) {
			logger.info(statePackage.state);
			saveStatePackage(statePackage);
		}
	}

	@Transactional
	public void saveStatePackage(StatePackage statePackage) {
		if (statePackage.diffBetweenOldAndNew != null) {
			logger.info("processing json diff.");
			jsonDiffProcessorService.process(statePackage.state, statePackage.diffBetweenOldAndNew);
		}
		save(statePackage.state);
	}
	
	@Transactional
	public void processStateChange(StatePackage statePackage) {
		jsonDiffProcessorService.process(statePackage.state, statePackage.diffBetweenOldAndNew);
	}

	public StateIndexInfo getHistorical(int index) {
		return frontEndStateDao.getHistorical(index);
	}
}
