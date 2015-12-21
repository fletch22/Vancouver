package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fletch22.orb.query.Criteria;

public class CriteriaByOrbTypeCollection {
	
	HashMap<Long, HashMap<Long, Criteria>> collection = new HashMap<Long, HashMap<Long, Criteria>>();
	
	public void put(Criteria criteria) {
		long id = criteria.getOrbTypeInternalId();
					
		HashMap<Long, Criteria> map = collection.get(id);
		
		if (map != null) {
			map.put(criteria.getCriteriaId(), criteria);
		} else {
			map = new HashMap<Long, Criteria>();
			map.put(criteria.getCriteriaId(), criteria);
			collection.put(id, map);
		}
	}
	
	public HashMap<Long, Criteria> remove(long orbTypeInternalId) {
		return collection.remove(orbTypeInternalId);
	}
	
	public void removeByCriteriaId(Criteria criteria) {
		Map<Long, Criteria> map = this.get(criteria.getOrbTypeInternalId());
		
		map.remove(criteria.getCriteriaId());
		if (map.size() == 0) {
			this.remove(criteria.getOrbTypeInternalId());
		}
	}
	
	public void clear() {
		this.collection.clear();
	}
	
	public HashMap<Long, Criteria> get(long orbTypeInternalId) {
		return collection.get(orbTypeInternalId);
	}
	
	public boolean doesExist(long orbTypeInternalId) {
		return collection.keySet().contains(orbTypeInternalId);
	}
}
