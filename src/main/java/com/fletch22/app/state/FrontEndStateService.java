package com.fletch22.app.state;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.fletch22.dao.LogActionService;
import com.fletch22.orb.command.transaction.RollbackTransactionService;
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
	
	@Autowired
	LogActionService logActionService;
	
	@Autowired
	RollbackTransactionService rollbackTransactionService;
	
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
		
		if (root.startupTimestamp.equals(statePackage.serverStartupTimestamp)) {
			if (statePackage.diffBetweenOldAndNew != null) {
				ArrayList<StuntDoubleAndNewId> stuntDoubleAndNewIdList = jsonDiffProcessorService.process(statePackage.state, statePackage.diffBetweenOldAndNew);
				statePackage.state = insertNewIdsIntoState(statePackage.state, stuntDoubleAndNewIdList);
			}
			logger.info("Saving state...");
			save(statePackage.state, statePackage.clientId);
		} else {
			throw new RestException(ErrorCode.CLIENT_THINKS_TALKING_TO_PREV_INSTANCE_OLD_SERVER);
		}
			
		return statePackage.state;
	}

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
		BigDecimal tranId = this.frontEndStateDao.getTranIdFromClientId(stateClientId);
		
		// NOTE: 11-05-2017: Kludgy. Although we use transaction start boundaries to help us roll back, multiple operations within the same 'transaction umbrella'
		// get their own transaction ID. Since the final transaction ID is created after we have a chance to read it, we have to call "getSubsequent".
		// It is assumed here that the subsequent and last transactionID is still part of the same "saveState" multi-persist operation.
		Optional<BigDecimal> tranIdFound = this.logActionService.getSubsequentTranIdIfAny(tranId);
		
		if (tranIdFound.isPresent()) {
			logger.info("Rolling back to tranID {} ...", tranIdFound.get());
			this.rollbackTransactionService.rollbackToSpecificTransaction(tranIdFound.get());
		}
	}
}
