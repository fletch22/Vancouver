package com.fletch22.app.state;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.command.transaction.TransactionService;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;

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

//		OrbType orbType = orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
//		Criteria criteria = new CriteriaStandard(orbType.id, "findFrontEndState");
//		
//		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo();
//		criteriaSortInfo.sortDirection = SortDirection.DESC;
//		criteriaSortInfo.sortAttributeName = FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID;
//		
//		criteria.setSortOrder(criteriaSortInfo);	
//		
//		OrbResultSet orbResultSet = this.queryManager.executeQuery(criteria);
		
		OrbType orbType = orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
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
}
