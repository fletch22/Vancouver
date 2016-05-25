package com.fletch22.app.state;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.CriteriaStandard;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.query.sort.SortInfo.SortDirection;
import com.fletch22.util.RandomUtil;

@Component
public class FrontEndStateDao {

	Logger logger = LoggerFactory.getLogger(FrontEndStateDao.class);

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	OrbManager orbManager;

	@Autowired
	TransactionService transactionService;

	@Autowired
	QueryManager queryManager;
	
	@Autowired
	RandomUtil randomUtil;

	public void save(String state, String clientId) {
		OrbType orbType = this.orbTypeManager
				.getOrbType(FrontEndState.TYPE_LABEL);

		Orb orb = orbManager.createUnsavedInitializedOrb(orbType);

		Map<String, String> properties = orb.getUserDefinedProperties();
		properties.put(FrontEndState.ATTR_STATE, state);
		properties.put(FrontEndState.ATTR_CLIENT_ID, clientId);

		BigDecimal currentTransactionId = transactionService
				.getCurrentTransactionId();

		logger.debug("Current tranid: {} for type: {}", currentTransactionId,
				orbType.id);

		if (currentTransactionId == TransactionService.NO_TRANSACTION_IN_FLIGHT) {
			throw new RuntimeException(
					"Attempted to save fron end state without a transaction number. This is not allowed. Wrap the call in a transaction.");
		}

		orb.getUserDefinedProperties().put(
				FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID,
				String.valueOf(currentTransactionId));

		orbManager.createOrb(orb);
	}

	public StateIndexInfo getHistorical(int index) {

		OrbResultSet orbResultSet = this.queryManager.executeQuery(FrontEndState.QUERY_GET_STATES);
		
		index = Math.abs(index);
		
		String result = null;
		int size = orbResultSet.orbList.size();
		
		StateIndexInfo stateIndexInfo = new StateIndexInfo();
		stateIndexInfo.indexOfMaxElement = size - 1;
		if (size > index) {
			Orb orb = orbResultSet.orbList.get(index);
			orb.getUserDefinedProperties();   
			result = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateIndexInfo.transactionId = new BigDecimal(orb.getUserDefinedProperties().get(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID));
		}
		
		logger.info("Getting element {}, when size: {}", index, size); 
		
		stateIndexInfo.state = result;
		if (size > 0) {
			stateIndexInfo.isEarliestState = (size <= index); 
		}
		
		return stateIndexInfo;
	}

	public StateIndexInfo getMostRecentHistorical() {
		
		OrbResultSet orbResultSet = this.queryManager.executeQuery(FrontEndState.QUERY_GET_STATES);

		int size = orbResultSet.orbList.size();
		StateIndexInfo stateIndexInfo = new StateIndexInfo();
		stateIndexInfo.indexOfMaxElement = 0;
		
		if (size > 0) {
			Orb orb = orbResultSet.orbList.get(stateIndexInfo.indexOfMaxElement);
			stateIndexInfo.state = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateIndexInfo.transactionId = new BigDecimal(orb.getUserDefinedProperties().get(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID));
		}
		
		logger.debug("Getting element {}, when size {}", size); 
		
		if (size > 0) {
			stateIndexInfo.isEarliestState = (size <= stateIndexInfo.indexOfMaxElement); 
		}
		
		return stateIndexInfo;
	}
	
	private Criteria createCriteriaFindByClientIds(List<String> clientIds) {
		OrbType orbType = orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
		Criteria criteria = new CriteriaStandard(orbType.id, randomUtil.getRandomUuidString());
		
		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo();
		criteriaSortInfo.sortDirection = SortDirection.DESC;
		criteriaSortInfo.sortAttributeName = FrontEndState.ATTR_CLIENT_ID;
		
		criteria.setSortOrder(criteriaSortInfo);
		
		logger.info("Number of client IDs {}", clientIds.size());
		
		criteria.addAnd(Constraint.in(FrontEndState.ATTR_CLIENT_ID, clientIds));
		
		return criteria;
	}
	
	public StateSearchResult determineLastGoodState(List<String> clientIds) {
		Criteria criteria = createCriteriaFindByClientIds(clientIds);
		OrbResultSet orbResultSet = this.queryManager.executeQuery(criteria);
		
		return determineLastGoodStateFromSortedList(clientIds, orbResultSet);
	}

	private StateSearchResult determineLastGoodStateFromSortedList(List<String> clientIds, OrbResultSet orbResultSet) {
		
		StateSearchResult stateSearchResult = new StateSearchResult();
		
		int size = orbResultSet.orbList.size();
		
		// All IDs present in result set. That means none were lost during the error state. 
		// We can infer that the last item in the result is the lexicographically
		// highest; therefore the most recent.
		if (size == clientIds.size()) {
			Orb orb = orbResultSet.orbList.get(clientIds.size() - 1);
			stateSearchResult.state = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
		} else {
			stateSearchResult = findLastGoodState(clientIds, orbResultSet);
		}
		
		return stateSearchResult;
	}

	// Find missing state if it exists in this set. Once found then use the previous "good" state.
	private StateSearchResult findLastGoodState(List<String> clientIdsList, OrbResultSet orbResultSet) {
		
		StateSearchResult stateSearchResult = new StateSearchResult();
		
		boolean isAtLeastOneClientIdFound = false;
		Orb orb = null;
		int resultSetSize = orbResultSet.getOrbList().size();
		int maxLoopCount = clientIdsList.size();
		
		for (int i = 0; i < maxLoopCount; i++) {
			if (i >= resultSetSize) {
				stateSearchResult.state = getPreviousOrbStateIfAvailable(orbResultSet, isAtLeastOneClientIdFound, i);
				break;
			}
			
			orb = orbResultSet.getOrbList().get(i);
			
			String clientIdFromClient = clientIdsList.get(i);
			String clientIdFromOrbDb = orb.getUserDefinedProperties().get(FrontEndState.ATTR_CLIENT_ID);
			
			if (!clientIdFromClient.equals(clientIdFromOrbDb)) {
				stateSearchResult.state = getPreviousOrbStateIfAvailable(orbResultSet, isAtLeastOneClientIdFound, i);
				break;
			} else {
				isAtLeastOneClientIdFound = true;	
			}
		}
		
		return stateSearchResult;
	}

	private String getPreviousOrbStateIfAvailable(OrbResultSet orbResultSet, boolean isAtLeastOneClientIdFound, int i) {
		String state = null;
		if (isAtLeastOneClientIdFound) {
			Orb orb = orbResultSet.getOrbList().get(i - 1);
			state = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
		}
		return state;
	}
	
	public static class StateSearchResult {
		public String state = null;

		public boolean isStateFound() {
			return (state != null);
		}
	}
}
