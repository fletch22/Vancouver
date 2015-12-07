package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fletch22.orb.query.Criteria;

public class CriteriaByOrbTypeCollection {
	
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
	
	public void removeByCriteriaId(Criteria criteria) {
		Collection<Criteria> collection = this.get(criteria.getOrbTypeInternalId());
		
		collection.remove(criteria);
		if (collection.size() == 0) {
			this.remove(criteria.getOrbTypeInternalId());
		}
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
