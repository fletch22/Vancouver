package com.fletch22.app.state;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;
import com.fletch22.app.designer.Root;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.state.FrontEndStateDao.StateSearchResult;
import com.fletch22.app.state.diff.service.JsonDiffProcessorService;
import com.fletch22.app.state.diff.service.StuntDoubleAndNewId;
import com.fletch22.web.controllers.ComponentController.ClientIdsPackage;
import com.fletch22.web.controllers.ComponentController.StatePackage;
import com.fletch22.web.controllers.exception.ErrorCode;
import com.fletch22.web.controllers.exception.RestException;

@Component
public class FrontEndStateService {
	
	Logger logger = LoggerFactory.getLogger(FrontEndStateService.class);
	
	@Autowired
	FrontEndStateDao frontEndStateDao;
	
	@Autowired
	JsonDiffProcessorService jsonDiffProcessorService;
	
	@Autowired
	AppContainerService appContainerService;
	
	@Autowired
	Root root;

	@Transactional
	public void save(String state, String clientId) {
		frontEndStateDao.save(state, clientId);
	}
	
	@Transactional
	public void save(List<StatePackage> statePackageList) {
		for (StatePackage statePackage : statePackageList) {
			logger.debug(statePackage.state); 
			saveStatePackage(statePackage);
		}
	}

	@Transactional
	public String saveStatePackage(StatePackage statePackage) {
		
		if (String.valueOf(root.startupTimestamp).equals(statePackage.serverStartupTimestamp)) {
			if (statePackage.diffBetweenOldAndNew != null) {
				ArrayList<StuntDoubleAndNewId> stuntDoubleAndNewIdList = jsonDiffProcessorService.process(statePackage.state, statePackage.diffBetweenOldAndNew);
				statePackage.state = insertNewIdsIntoState(statePackage.state, stuntDoubleAndNewIdList);
			}
			save(statePackage.state, statePackage.clientId);
		} else {
			throw new RestException(ErrorCode.CLIENT_THINKS_TALKING_TO_PREV_INSTANCE_OLD_SERVER);
		}
			
		return statePackage.state;
	}
	
//	private boolean replaceIds(String state, StuntDoubleAndNewId stuntDoubleAndNewId) {
//		int start = state.indexOf(stuntDoubleAndNewId.temporaryId) - 1;
//		int end = start + stuntDoubleAndNewId.temporaryId.length() + 2;
//		
//		boolean wasFound = (start != -1);
//		if (wasFound) {
//			state = state.substring(0, start) + String.valueOf(stuntDoubleAndNewId.idNew) + state.substring(end, state.length());
//		}
//		
//		return wasFound;
//	}
	
	private String insertNewIdsIntoState(String state, ArrayList<StuntDoubleAndNewId> stuntDoubleAndNewIdList) {
		for (StuntDoubleAndNewId stuntDoubleAndNewId : stuntDoubleAndNewIdList) {
			state = state.replace("\"" + stuntDoubleAndNewId.temporaryId + "\"", String.valueOf(stuntDoubleAndNewId.idNew));
		}
		
		return state;
	}

	public StateIndexInfo getHistorical(int index) {
		return frontEndStateDao.getHistorical(index);  
	}

	public StateIndexInfo getMostRecentHistorical() {
		return frontEndStateDao.getMostRecentHistorical();
	}

	public StateIndexInfo getEarliestState() {
		return frontEndStateDao.getEarliestState();
	}

	public StateSearchResult determineLastGoodState(ClientIdsPackage clientIdsPackage) {
		
		StateSearchResult stateSearchResult = new StateSearchResult();
		
		// Work through the outer array in ascending order.
		for (List<String> packages : clientIdsPackage.idPackages) {
			stateSearchResult = frontEndStateDao.determineLastGoodState(packages);
			if (stateSearchResult.isStateFound()) {
				break;
			}
		}
		
		return stateSearchResult;
	}

	public void rollbackToState(String stateClientId) {
		frontEndStateDao.rollbackToState(stateClientId);
	}
}
