package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fletch22.orb.query.Criteria;

public abstract class CriteriaCollection {
	
	protected Map<Long, Criteria> criteriaByIdMap = new HashMap<Long, Criteria>();
	protected CriteriaByOrbTypeCollection criteriaByOrbTypeCollection = new CriteriaByOrbTypeCollection();
	
	public boolean doesQueryExist(long queryInternalId) {
		return criteriaByIdMap.containsKey(queryInternalId);
	}

	public abstract void add(Criteria criteria);

	public abstract Criteria removeByCriteriaId(long id);

	public abstract List<Criteria> removeByOrbTypeId(long id);

	public abstract void clear();

	public Criteria getByQueryId(long orbInternalIdQuery) {
		return this.criteriaByIdMap.get(orbInternalIdQuery);
	}
	
	public List<Criteria> getByOrbTypeInsideCriteria(long orbInternalId) {
		
		List<Criteria> criteria = this.criteriaByOrbTypeCollection.get(orbInternalId);
		return (criteria == null) ? new ArrayList<Criteria>() : criteria;
	}
	
	public Set<Long> getKeys() {
		return criteriaByIdMap.keySet();
	}
	
	public long getSize() {
		return criteriaByIdMap.size();
	}
	
	public boolean doesQueryWithLabelExist(String label) {
		
		boolean doesExist = false;
		Set<Long> keys = getKeys();
		for (Long key: keys) {
			Criteria criteriaFound = getByQueryId(key);
			if (criteriaFound.getLabel().equals(label)) {
				doesExist = true;
			}
		}
		return doesExist;
	}
	
	public Criteria findByLabel(String label) {
		
		Criteria criteriaFound = null;
		Set<Long> keys = getKeys();
		for (Long key: keys) {
			Criteria criteria = getByQueryId(key);
			if (criteria.getLabel().equals(label)) {
				criteriaFound = criteria;
				break;
			}
		}
		return criteriaFound;
	}
	
	public boolean doesCriteriaExistWithOrbTypeInternalId(long orbTypeInternalId) {
		return this.criteriaByOrbTypeCollection.doesExist(orbTypeInternalId);
	}
	
	public void validateCriteria(Criteria criteriaToValidate) {

		String message = null;
		long id = criteriaToValidate.getCriteriaId();

		if (id == Criteria.UNSET_ID) {
			message = String.format("Encountered a problem. Criteria has id %s. This means criteria ID is unset.", id);
			throw new RuntimeException(message);
		}

		if (criteriaByIdMap.containsKey(id)) {
			message = String.format("Encountered a problem. Criteria with id '%s' already exists.", id);
			throw new RuntimeException(message);
		} else {

			String label = criteriaToValidate.getLabel();
			if (label == null) {
				message = "Criteria's label cannot be null.";
				throw new RuntimeException(message);
			}

			Set<Long> keys = this.criteriaByIdMap.keySet();
			for (Long key : keys) {
				Criteria criteria = this.criteriaByIdMap.get(key);
				if (criteriaToValidate.getOrbTypeInternalId() == criteria.getOrbTypeInternalId() && criteria.getLabel().equals(label)) {
					message = String.format("Encountered a problem. Criteria with with type id '%s' and label '%s' already exists.", criteria.getOrbTypeInternalId(), label);
					throw new RuntimeException(message);
				}
			}
		}
	}
}
