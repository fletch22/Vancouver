package com.fletch22.app.state;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.state.diff.service.JsonDiffProcessorService;
import com.fletch22.app.state.diff.service.StuntDoubleAndNewId;
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
	public String saveStatePackage(StatePackage statePackage) {
		if (statePackage.diffBetweenOldAndNew != null) {
			ArrayList<StuntDoubleAndNewId> stuntDoubleAndNewIdList = jsonDiffProcessorService.process(statePackage.state, statePackage.diffBetweenOldAndNew);
			statePackage.state = insertNewIdsIntoState(statePackage.state, stuntDoubleAndNewIdList);
		}
		
		logger.debug(statePackage.state);
		
		save(statePackage.state);
		
		return statePackage.state;
	}
	
	private String insertNewIdsIntoState(String state, ArrayList<StuntDoubleAndNewId> stuntDoubleAndNewIdList) {
		
		StringBuffer sbState = new StringBuffer(state);
		for (StuntDoubleAndNewId stuntDoubleAndNewId : stuntDoubleAndNewIdList) {
			
			int start = sbState.indexOf(stuntDoubleAndNewId.temporaryId) - 1;
			int end = start + stuntDoubleAndNewId.temporaryId.length() + 2;
			
			sbState = sbState.replace(start, end, String.valueOf(stuntDoubleAndNewId.idNew));
		}
		
		return sbState.toString();
	}

	public StateIndexInfo getHistorical(int index) {
		return frontEndStateDao.getHistorical(index);
	}

	public StateIndexInfo getMostRecentHistorical() {
		return frontEndStateDao.getMostRecentHistorical();
	}
}
