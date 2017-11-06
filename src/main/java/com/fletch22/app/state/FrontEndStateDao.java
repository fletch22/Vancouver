package com.fletch22.app.state;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Root;
import com.fletch22.dao.LogActionService;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.command.transaction.RollbackTransactionService;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaStandard;
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
	RollbackTransactionService rollbackTransactionService;

	@Autowired
	QueryManager queryManager;

	@Autowired
	RandomUtil randomUtil;

	@Autowired
	Root root;
	
	@Autowired
	LogActionService logActionService;

	public void save(String state, String clientId) {
		OrbType orbType = this.orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);

		Orb orb = orbManager.createUnsavedInitializedOrb(orbType);

		Map<String, String> properties = orb.getUserDefinedProperties();

		logger.debug("State saved: " + state);

		properties.put(FrontEndState.ATTR_STATE, state);
		properties.put(FrontEndState.ATTR_CLIENT_ID, clientId);

		BigDecimal currentTransactionId = transactionService.generateTranId();
		logger.debug("Current tranId: {} for {}", currentTransactionId, clientId);

		if (currentTransactionId == TransactionService.NO_TRANSACTION_IN_FLIGHT) {
			throw new RuntimeException("Attempted to save fron end state without a transaction number. This is not allowed. Wrap the call in a transaction.");
		}

		orb.getUserDefinedProperties().put(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID, String.valueOf(currentTransactionId));

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
			result = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateIndexInfo.clientId = orb.getUserDefinedProperties().get(FrontEndState.ATTR_CLIENT_ID);
			stateIndexInfo.indexOfReturnedState = index;
		}

		stateIndexInfo.state = result;
		if (size > 0) {
			stateIndexInfo.isEarliestState = (size <= index);
		}

		stateIndexInfo.startupTimestamp = root.startupTimestamp;

		return stateIndexInfo;
	}

	public StateIndexInfo getMostRecentHistorical() {

		StateHistory stateHistory = new StateHistory();

		int size = stateHistory.getSize();
		StateIndexInfo stateIndexInfo = new StateIndexInfo();
		stateIndexInfo.indexOfMaxElement = 0;

		if (size > 0) {
			Orb orb = stateHistory.getOrbMostRecent();
			stateIndexInfo.state = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateIndexInfo.clientId = orb.getUserDefinedProperties().get(FrontEndState.ATTR_CLIENT_ID);
			stateIndexInfo.indexOfReturnedState = 0;
		}

		logger.info("Getting element {}, when size {}", stateIndexInfo.clientId, size);

		if (size > 0) {
			stateIndexInfo.isEarliestState = (size <= stateIndexInfo.indexOfMaxElement);
		}

		stateIndexInfo.startupTimestamp = root.startupTimestamp;

		return stateIndexInfo;
	}

	private Orb getState(int ordinal) {
		OrbResultSet orbResultSet = this.queryManager.executeQuery(FrontEndState.QUERY_GET_STATES);
		return orbResultSet.orbList.get(ordinal);
	}

	public class StateHistory {
		private static final int ORDINAL_MOST_RECENT = 0;
		private OrbResultSet orbResultSet = null;

		public StateHistory() {
			this.orbResultSet = queryManager.executeQuery(FrontEndState.QUERY_GET_STATES);
		}

		public Orb getOrbMostRecent() {
			Orb orb = null;

			if (getSize() > 0) {
				orb = this.orbResultSet.getOrbList().get(ORDINAL_MOST_RECENT);
			}

			return orb;
		}

		public Orb getOrbOldest() {
			Orb orb = null;

			int size = getSize();
			if (size > 0) {
				orb = this.orbResultSet.getOrbList().get(size - 1);
			}

			return orb;
		}

		public int getSize() {
			return this.orbResultSet.getOrbList().size();
		}

		public boolean hasARecentState() {
			return (getSize() > 0);
		}
	}

	private Criteria createCriteriaFindByClientIds(List<String> clientIds) {
		OrbType orbType = orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
		Criteria criteria = new CriteriaStandard(orbType.id, randomUtil.getRandomUuidString());

		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo();
		criteriaSortInfo.sortDirection = SortDirection.DESC;
		criteriaSortInfo.sortAttributeName = FrontEndState.ATTR_CLIENT_ID;

		criteria.setSortOrder(criteriaSortInfo);

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

		logger.debug("Found number of states from passed in from client: {}", size);

		// All IDs present in result set. That means none were lost during the
		// error state.
		// We can infer that the last item in the result is the
		// lexicographically
		// highest; therefore the most recent.
		if (size == clientIds.size()) {
			Orb orb = orbResultSet.orbList.get(clientIds.size() - 1);
			stateSearchResult.state = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateSearchResult.clientId = orb.getUserDefinedProperties().get(FrontEndState.ATTR_CLIENT_ID);
		} else {
			stateSearchResult = findLastGoodState(clientIds, orbResultSet);
		}

		return stateSearchResult;
	}

	// Find missing state if it exists in this set. Once found then use the
	// previous "good" state.
	private StateSearchResult findLastGoodState(List<String> clientIdsList, OrbResultSet orbResultSet) {

		StateSearchResult stateSearchResult = new StateSearchResult();

		boolean isAtLeastOneClientIdFound = false;
		Orb orb = null;
		int resultSetSize = orbResultSet.getOrbList().size();
		int maxLoopCount = clientIdsList.size();

		if (resultSetSize == 0) {
			stateSearchResult = getMostRecenState();
		} else {
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
					stateSearchResult.clientId = clientIdFromOrbDb;
					break;
				} else {
					isAtLeastOneClientIdFound = true;
				}
			}
		}

		return stateSearchResult;
	}

	private StateSearchResult getMostRecenState() {
		StateSearchResult stateSearchResult = new StateSearchResult();
		StateHistory stateHistory = new StateHistory();

		logger.debug("Number states found: {}", stateHistory.getSize());

		if (stateHistory.hasARecentState()) {
			Orb orbMostRecent = stateHistory.getOrbMostRecent();

			stateSearchResult.state = orbMostRecent.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateSearchResult.clientId = orbMostRecent.getUserDefinedProperties().get(FrontEndState.ATTR_CLIENT_ID);
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
		public String clientId;

		public boolean isStateFound() {
			return (state != null);
		}
	}

	public BigDecimal getTranIdFromClientId(String stateClientId) {
		OrbType orbType = this.orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
		Orb orbOrginal = this.queryManager.findByAttribute(orbType.id, FrontEndState.ATTR_CLIENT_ID, stateClientId).uniqueResult();
		return new BigDecimal(orbOrginal.getUserDefinedProperties().get(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID));
	}

	private Optional<BigDecimal> getSubsequentState(long orbTypeInternalId, String stateClientId) {
		Orb orbOrginal = this.queryManager.findByAttribute(orbTypeInternalId, FrontEndState.ATTR_CLIENT_ID, stateClientId).uniqueResult();

		BigDecimal tranId = new BigDecimal(orbOrginal.getUserDefinedProperties().get(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID));

		Criteria criteria = new CriteriaStandard(orbTypeInternalId, randomUtil.getRandomUuidString());

		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo();
		criteriaSortInfo.sortDirection = SortDirection.DESC;
		criteriaSortInfo.sortAttributeName = FrontEndState.ATTR_CLIENT_ID;

		criteria.setSortOrder(criteriaSortInfo);

		criteria.addAnd(Constraint.gt(FrontEndState.ATTR_CLIENT_ID, stateClientId));

		OrbResultSet orbResultSet = this.queryManager.executeQuery(criteria);

		Optional<BigDecimal> optional = Optional.empty();
		if (orbResultSet.orbList.size() == 0) {
			logger.error("There were no subsequent states.");
		} else {
			Orb orbSubsequent = orbResultSet.getOrbList().get(0);
			BigDecimal tranIdSubsequent = new BigDecimal(orbSubsequent.getUserDefinedProperties().get(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID));
			optional = Optional.of(tranIdSubsequent);
		}
		return optional;
	}

	public StateIndexInfo getEarliestState() {
		StateIndexInfo stateIndexInfo = new StateIndexInfo();

		StateHistory stateHistory = new StateHistory();

		if (stateHistory.hasARecentState()) {
			Orb orb = stateHistory.getOrbOldest();
			int size = stateHistory.getSize();
			stateIndexInfo.indexOfMaxElement = size - 1;
			stateIndexInfo.indexOfReturnedState = stateIndexInfo.indexOfMaxElement;
			stateIndexInfo.isEarliestState = true;
			stateIndexInfo.state = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateIndexInfo.clientId = orb.getUserDefinedProperties().get(FrontEndState.ATTR_CLIENT_ID);
		}

		stateIndexInfo.startupTimestamp = root.startupTimestamp;

		return stateIndexInfo;
	}
}
