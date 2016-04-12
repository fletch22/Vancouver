package com.fletch22.app.state;

import java.math.BigDecimal;

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
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.query.sort.SortInfo.SortDirection;

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

	public void save(String state) {
		OrbType orbType = this.orbTypeManager
				.getOrbType(FrontEndState.TYPE_LABEL);

		Orb orb = orbManager.createUnsavedInitializedOrb(orbType);

		orb.getUserDefinedProperties().put(FrontEndState.ATTR_STATE, state);

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

		OrbType orbType = orbTypeManager.getOrbType(FrontEndState.TYPE_LABEL);
		Criteria criteria = new CriteriaStandard(orbType.id, "findFrontEndState");
		
		CriteriaSortInfo criteriaSortInfo = new CriteriaSortInfo();
		criteriaSortInfo.sortDirection = SortDirection.DESC;
		criteriaSortInfo.sortAttributeName = FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID;
		
		criteria.setSortOrder(criteriaSortInfo);	
		
		OrbResultSet orbResultSet = this.queryManager.executeQuery(criteria);
		
		index = Math.abs(index);
		
		String result = null;
		int size = orbResultSet.orbList.size();
		
		StateIndexInfo stateIndexInfo = new StateIndexInfo();
		if (size > index) {
			Orb orb = orbResultSet.orbList.get(index);
			orb.getUserDefinedProperties();
			result = orb.getUserDefinedProperties().get(FrontEndState.ATTR_STATE);
			stateIndexInfo.transactionId = Long.parseLong(orb.getUserDefinedProperties().get(FrontEndState.ATTR_ASSOCIATED_TRANSACTION_ID));
		}
		
		logger.info("Getting element {}, when size: {}", index, size);
		
		stateIndexInfo.state = result;
		if (size > 0) {
			stateIndexInfo.isEarliestState = (size == index);
		}
		
		return stateIndexInfo;
	}
}
