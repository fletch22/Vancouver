package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fletch22.orb.query.CriteriaImpl;

public class CriteriaByOrbTypeCollection {
	
	Map<Long, List<CriteriaImpl>> collection = new HashMap<Long, List<CriteriaImpl>>();
	
	public void add(CriteriaImpl criteria) {
		long id = criteria.getOrbTypeInternalId();
					
		List<CriteriaImpl> list = collection.get(id);
		
		if (list != null) {
			list.add(criteria);
		} else {
			list = new ArrayList<CriteriaImpl>();
			list.add(criteria);
			collection.put(id, list);
		}
	}
	
	public List<CriteriaImpl> remove(long orbTypeInternalId) {
		return collection.remove(orbTypeInternalId);
	}
	
	public void removeByCriteriaId(CriteriaImpl criteria) {
		Collection<CriteriaImpl> collection = this.get(criteria.getOrbTypeInternalId());
		
		collection.remove(criteria);
		if (collection.size() == 0) {
			this.remove(criteria.getOrbTypeInternalId());
		}
	}
	
	public void clear() {
		this.collection.clear();
	}
	
	public List<CriteriaImpl> get(long orbTypeInternalId) {
		return collection.get(orbTypeInternalId);
	}
	
	public boolean doesExist(long orbTypeInternalId) {
		return collection.keySet().contains(orbTypeInternalId);
	}
}
