package com.fletch22.orb.query;


import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.systemType.SystemType;

@Component
public class QueryManagerImpl implements QueryManager {
	
	Logger logger = LoggerFactory.getLogger(QueryManagerImpl.class);
	
	@Autowired
	Cache cache;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	QueryAttributeRenameHandler queryAttributeRenameHandler;
	
	@Autowired
	CriteriaFactory criteriaFactory;

	@Loggable4Event
	@Override
	public long create(Criteria criteria) {
		
		OrbType orbType = getQueryOrbType();
		Orb orb = orbManager.createOrb(orbType.id); 
		
		addQueryToCollection(orb.getOrbInternalId(), criteria);
		
		return orb.getOrbInternalId();
	}

	private OrbType getQueryOrbType() {
		return orbTypeManager.getOrbType(SystemType.QUERY.getLabel());
	}
	
	@Loggable4Event
	@Override
	public void create(Orb orb, Criteria criteria) {
		
		validateQueryOrb(orb);

		orbManager.createOrb(orb);
		addQueryToCollection(orb.getOrbInternalId(), criteria);
	}
	
	@Loggable4Event
	@Override
	public void addQueryToCollection(long orbInternalIdQuery, Criteria criteria) {
		cache.queryCollection.add(orbInternalIdQuery, criteria);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		removeQueryFromCollection(orbInternalIdQuery);
	}
	
	@Loggable4Event
	@Override
	public void removeQueryFromCollection(long orbInternalIdQuery) {
		Criteria criteria = cache.queryCollection.remove(orbInternalIdQuery);
		
		if (criteria != null)  {
			Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
			addQueryToCollection(orbInternalIdQuery, criteria);
		}
	}
	
	public boolean doesQueryExist(long orbInternalIdQuery) {
		return cache.queryCollection.doesQueryExist(orbInternalIdQuery);
	}
	
	private void validateQueryOrb(Orb orb) {
		if (SystemType.QUERY.getId() != orb.getOrbTypeInternalId()) {
			throw new RuntimeException("Query orb is not of correct type.");
		}
	}

	@Loggable4Event
	@Override
	public void delete(long orbInternalIdQuery, boolean isDeleteDependencies) {
		orbManager.deleteOrbIgnoreQueryDependencies(orbInternalIdQuery, isDeleteDependencies);
		removeQueryFromCollection(orbInternalIdQuery);
	}

	@Override
	public void deleteAllQueries() {
		cache.queryCollection.deleteAllQueries();
	}

	@Override
	public Criteria get(long orbInternalIdQuery) {
		return cache.queryCollection.get(orbInternalIdQuery);
	}
	
	@Override
	public void handleAttributeRename(long orbTypeInternalId, String attributeOldName, String attributeNewName) {
		queryAttributeRenameHandler.handleAttributeRename(orbTypeInternalId, attributeOldName, attributeNewName);
	}

	@Override
	public void handleAttributeDeletion(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		QueryCollection queryCollection = cache.queryCollection;
		
		Set<Long> criteriaKey = queryCollection.getKeys();
		for (long key : criteriaKey) {
			Criteria criteria = queryCollection.get(key);
			
			List<LogicalConstraint> logicalConstraintList = criteria.logicalConstraintList;
			for (LogicalConstraint logicalConstraint : logicalConstraintList) {
				handleAttributeDeletion(logicalConstraint, key, attributeName, isDeleteDependencies);
			}
		}
	}

	private void handleAttributeDeletion(LogicalConstraint logicalConstraint, long orbInternalIdQuery, String attributeName, boolean isDeleteDependencies) {
		
		boolean hasAttribute = hasAttribute(logicalConstraint, attributeName);
		if (hasAttribute) {
			if (isDeleteDependencies) {
				this.delete(orbInternalIdQuery, isDeleteDependencies);
			} else {
				throw new RuntimeException("The attribute has a dependent query. System will not allow deletion.");
			}
		}
	}
	
	private boolean hasAttribute(LogicalConstraint logicalConstraint, String attributeName) {
		Constraint[] constraintArray = logicalConstraint.constraint.getConstraints();
		boolean hasAttribute = false;
		
		for (Constraint constraintInner: constraintArray) {
			
			if (constraintInner instanceof ConstraintDetailsSingleValue) {
				hasAttribute = hasAttribute((ConstraintDetails) constraintInner, attributeName);
			} else if (constraintInner instanceof ConstraintDetailsList) {
				hasAttribute = hasAttribute((ConstraintDetails) constraintInner, attributeName);
			} else if (constraintInner instanceof ConstraintCollection) {
				LogicalConstraint logicalConstraintLocal = new LogicalConstraint(logicalConstraint.logicalOperator, constraintInner);
				hasAttribute = hasAttribute(logicalConstraintLocal, attributeName);
			} else if (constraintInner instanceof LogicalConstraint) {
				hasAttribute = hasAttribute((LogicalConstraint) constraintInner, attributeName);
			}
			
			if (hasAttribute) {
				break;
			}
		}
		
		return hasAttribute;
	}
	
	private boolean hasAttribute(ConstraintDetails constraintDetails, String attributeName) {
		boolean hasAttribute = false;
		if (constraintDetails.attributeName.equals(attributeName)) {
			hasAttribute = true;
		}
		
		return hasAttribute;
	}

	@Override
	public ResultSet executeQuery(String queryLabel) {
		
		Criteria criteria = findQuery(queryLabel);
		return cache.orbCollection.executeQuery(criteria);
	}

	// TODO: 09-28-2015: Consider performing an actual query to get query here.
	//	Criteria criteria = criteriaFactory.getInstance(getQueryOrbType(), "findQuery");
	//	criteria.add(Constraint.eq(SystemType.QUERY_INSTANCE_ATTRIBUTE_LABEL, queryLabel));
	//	
	//	ResultSet resultSet = cache.orbCollection.executeQuery(criteria);
	protected Criteria findQuery(String queryLabel) {
		Criteria criteria = null;
		OrbType orbType = getQueryOrbType();
		List<Orb> orbList = orbManager.getOrbsOfType(orbType.id);
		for (Orb orb : orbList) {
			if (orb.getUserDefinedProperties().get(SystemType.QUERY_INSTANCE_ATTRIBUTE_LABEL).equals(queryLabel)) {
				criteria = get(orb.getOrbInternalId());
				break;
			}
		}
		return criteria;
	}
}
