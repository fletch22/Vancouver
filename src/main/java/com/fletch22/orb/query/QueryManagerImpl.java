package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

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
	CriteriaAttributeRenameHandler criteriaAttributeRenameHandler;

	@Autowired
	CriteriaAttributeDeleteHandler criteriaAttributeDeleteHandler;

	@Autowired
	CriteriaFactory criteriaFactory;

	@Override
	public long create(Criteria criteria) {

		OrbType orbType = getQueryOrbType();

		Orb orb = new Orb();
		orb.setOrbTypeInternalId(orbType.id);

		orb = orbManager.createOrb(orb);
		criteria.setId(orb.getOrbInternalId());

		addToCollection(criteria);

		return orb.getOrbInternalId();
	}

	private OrbType getQueryOrbType() {
		return orbTypeManager.getOrbType(SystemType.CRITERIA.getLabel());
	}

	@Loggable4Event
	@Override
	public void addToCollection(Criteria criteria) {
		cache.queryCollection.add(criteria);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		removeFromCollection(criteria.getCriteriaId());
	}

	@Loggable4Event
	@Override
	public void removeFromCollection(long criteriaId) {
		Criteria criteria = cache.queryCollection.removeByCriteriaId(criteriaId);

		if (criteria != null) {
			Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
			addToCollection(criteria);
		}
	}

	public boolean doesQueryExist(long criteriaId) {
		return cache.queryCollection.doesQueryExist(criteriaId);
	}

	// FIXME: 11-12-2014: This is odd. Reconcile with "remove" method.
	@Override
	public void delete(long criteriaId, boolean isDeleteDependencies) {
		orbManager.deleteOrbIgnoreQueryDependencies(criteriaId, isDeleteDependencies);
		removeFromCollection(criteriaId);
	}

	@Override
	public void nukeAllQueries() {
		cache.queryCollection.clear();
	}

	@Override
	public Criteria get(long criteriaId) {
		return cache.queryCollection.getByQueryId(criteriaId);
	}

	@Override
	public void handleAttributeRenameEvent(long orbTypeInternalId, String attributeOldName, String attributeNewName) {
		criteriaAttributeRenameHandler.handleAttributeRename(orbTypeInternalId, attributeOldName, attributeNewName);
	}

	@Override
	public void handleAttributeDeleteEvent(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		criteriaAttributeDeleteHandler.handleAttributeDeletion(orbTypeInternalId, attributeName, isDeleteDependencies);
	}

	@Override
	public OrbResultSet executeQuery(long orbTypeInternalId, String queryLabel) {

		Criteria criteria = findQuery(orbTypeInternalId, queryLabel);
		return cache.orbCollection.executeQuery(criteria);
	}

	@Override
	public OrbResultSet executeQuery(Criteria criteria) {
		return cache.orbCollection.executeQuery(criteria);
	}

	protected Criteria findQuery(long criteriaOrbTypeInternalId, String queryLabel) {

		boolean isCriteriaFound = false;
		Criteria criteriaFound = null;
		OrbType orbType = getQueryOrbType();
		List<Orb> orbList = orbManager.getOrbsOfType(orbType.id);
		for (Orb orb : orbList) {
			criteriaFound = get(orb.getOrbInternalId());
			if (criteriaFound.getLabel().equals(queryLabel)
			&& criteriaFound.getOrbTypeInternalId() == criteriaOrbTypeInternalId) {
				isCriteriaFound = true;
				break;
			}
		}

		if (!isCriteriaFound) {
			throw new RuntimeException(String.format("Encountered problem trying to find query. Couldn't find query '%s'.", queryLabel));
		}

		return criteriaFound;
	}

	@Override
	public OrbResultSet findByAttribute(long orbTypeInternalId, String attributeName, String attributeValueToFind) {

		OrbType orbType = orbTypeManager.getOrbType(orbTypeInternalId);
		Criteria criteria = criteriaFactory.createInstance(orbType, "findByAttribute");
		criteria.addAnd(Constraint.eq(attributeName, attributeValueToFind));

		return executeQuery(criteria);
	}

	@Override
	public void handleTypeDeleteEvent(long orbTypeInternalId, boolean isDeleteDependencies) {

		boolean doesCriteriaExist = cache.queryCollection.doesCriteriaExistWithOrbTypeInternalId(orbTypeInternalId);
		if (doesCriteriaExist && isDeleteDependencies) {
			deleteAllCriteriaBackedOrbsRelatedToType(orbTypeInternalId, isDeleteDependencies);
		} else {
			throw new RuntimeException("Encountered problem handling orb type delete in query manager. Orb type has a dependency in the query collection. There is a criteria whose internal orb type internal ID references the orb type you are trying to delete.");
		}
	}

	private void deleteAllCriteriaBackedOrbsRelatedToType(long orbTypeInternalId, boolean isDeleteDependencies) {
		for (long id: getCriteriaIdsByTypeIds(orbTypeInternalId)) {
			orbManager.deleteOrb(id, isDeleteDependencies);
		}
	}

	private List<Long> getCriteriaIdsByTypeIds(long orbTypeInternalId) {
		List<Criteria> criteriaList = cache.queryCollection.getByOrbTypeInsideCriteria(orbTypeInternalId);
		List<Long> idList = new ArrayList<Long>();
		for (Criteria criteria : criteriaList) {
			idList.add(criteria.getCriteriaId());
		}
		return idList;
	}

	@Override
	public void handleInstanceDeleteEvent(long orbInternalId, boolean isDeleteDependencies) {
		if (isDeleteDependencies) {
			removeFromCollection(orbInternalId);
		} else {
			boolean doesExist = doesQueryExist(orbInternalId);
			if (doesExist) {
				String message = String.format("Encountered problem deleting orb '%s'. Orb has at least one dependency. A query exists that depends on the orb.", orbInternalId);
				throw new RuntimeException(message);
			}
		}
	}
}
