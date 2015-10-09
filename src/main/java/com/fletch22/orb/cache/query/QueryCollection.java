package com.fletch22.orb.cache.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
@Scope("prototype")
public class QueryCollection {

	private Map<Long, Criteria> queries = new HashMap<Long, Criteria>();
	
	public boolean doesQueryExist(long queryInternalId) {
		return queries.containsKey(queryInternalId);
	}
	
	public void add(long id, Criteria criteria) {
		validateCriteria(id, criteria);
		queries.put(id, criteria);
	}
	
	public Criteria remove(long id) {
		return queries.remove(id);
	}
	
	public void deleteAllQueries() {
		this.queries.clear();
	}

	public Criteria get(long orbInternalIdQuery) {
		return this.queries.get(orbInternalIdQuery);
	}
	
	public Set<Long> getKeys() {
		return queries.keySet();
	}
	
	public long getSize() {
		return queries.size();
	}
	
	private void validateCriteria(long id, Criteria criteriaToValidate) {
		String message = null;
		if (queries.containsKey(id))  {
			message = String.format("Encountered a problem. Criteria with id '%s' already exists.", id);
			throw new RuntimeException(message);
		} else {
			
			String label = criteriaToValidate.getLabel();
			if (label == null) {
				message = "Criteria's label cannot be null.";
				throw new RuntimeException(message);
			}
			
			Set<Long> keys = this.queries.keySet();
			for (Long key: keys) {
				Criteria criteria = this.queries.get(key);
				if (criteria.getLabel().equals(label)) {
					message = String.format("Encountered a problem. Criteria with label '%s' already exists.", label);
					throw new RuntimeException(message);
				}
			}
		}
	}
	
	public boolean doesQueryWithLabelExist(String label) {
		boolean doesExist = false;
		Set<Long> keys = getKeys();
		for (Long key: keys) {
			Criteria criteriaFound = get(key);
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
			Criteria criteria = get(key);
			if (criteria.getLabel().equals(label)) {
				criteriaFound = criteria;
				break;
			}
		}
		return criteriaFound;
	}
}
