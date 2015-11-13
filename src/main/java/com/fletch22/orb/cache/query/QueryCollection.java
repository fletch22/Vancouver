package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
@Scope("prototype")
public class QueryCollection {
	
	static Logger logger = LoggerFactory.getLogger(QueryCollection.class);

	private Map<Long, Criteria> queries = new HashMap<Long, Criteria>();
	private QueryCollectionByOrbType queriesByOrbType = new QueryCollectionByOrbType();
	
	public boolean doesQueryExist(long queryInternalId) {
		return queries.containsKey(queryInternalId);
	}
	
	public void add(Criteria criteria) {
		validateCriteria(criteria);
		queries.put(criteria.getCriteriaId(), criteria);
		queriesByOrbType.add(criteria);
	}
	
	public Criteria removeByCriteriaId(long id) {
		Criteria criteria = queries.remove(id);
		Collection<Criteria> collection = queriesByOrbType.get(criteria.getOrbTypeInternalId());
		
		collection.remove(criteria);
		if (collection.size() == 0) {
			queriesByOrbType.remove(criteria.getOrbTypeInternalId());
		}
				
		return criteria;
	}
	
	public List<Criteria> removeByOrbTypeId(long id) {
		List<Criteria> criteriaList = queriesByOrbType.remove(id);
		for (Criteria criteria : criteriaList) {
			queries.remove(criteria.getCriteriaId());
		}
		return criteriaList;
	}
	
	public void clear() {
		this.queries.clear();
		this.queriesByOrbType.clear();
	}

	public Criteria getByQueryId(long orbInternalIdQuery) {
		return this.queries.get(orbInternalIdQuery);
	}
	
	public List<Criteria> getByOrbTypeInsideCriteria(long orbInternalId) {
		return this.queriesByOrbType.get(orbInternalId);
	}
	
	public Set<Long> getKeys() {
		return queries.keySet();
	}
	
	public long getSize() {
		return queries.size();
	}
	
	private void validateCriteria(Criteria criteriaToValidate) {
		
		String message = null;
		long id = criteriaToValidate.getCriteriaId();
		
		if (id == Criteria.UNSET_CRITERIA_ID) {
			message = String.format("Encountered a problem. Criteria has id %s. This means criteria ID is unset.", id);
			throw new RuntimeException(message);
		}
		
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
				logger.info("Key: {}", key);
				Criteria criteria = this.queries.get(key);
				if (criteriaToValidate.getOrbTypeInternalId() == criteria.getOrbTypeInternalId()
				&& criteria.getLabel().equals(label)) {
					message = String.format("Encountered a problem. Criteria with with type id '%s' and label '%s' already exists.", criteria.getOrbTypeInternalId(), label);
					throw new RuntimeException(message);
				}
			}
		}
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
		return this.queriesByOrbType.doesExist(orbTypeInternalId);
	}
	
	private class QueryCollectionByOrbType {
		
		Map<Long, List<Criteria>> collection = new HashMap<Long, List<Criteria>>();
		
		public void add(Criteria criteria) {
			long id = criteria.getOrbTypeInternalId();
						
			List<Criteria> list = collection.get(id);
			
			if (list != null) {
				list.add(criteria);
			} else {
				list = new ArrayList<Criteria>();
				list.add(criteria);
				collection.put(id, list);
			}
		}
		
		public List<Criteria> remove(long orbTypeInternalId) {
			return collection.remove(orbTypeInternalId);
		}
		
		public void clear() {
			this.collection.clear();
		}
		
		public List<Criteria> get(long orbTypeInternalId) {
			return collection.get(orbTypeInternalId);
		}
		
		public boolean doesExist(long orbTypeInternalId) {
			return collection.keySet().contains(orbTypeInternalId);
		}
	}
}
