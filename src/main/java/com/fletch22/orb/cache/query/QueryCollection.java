package com.fletch22.orb.cache.query;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.Criteria;

@Component
@Scope("prototype")
public class QueryCollection extends CriteriaCollection {
	
	static Logger logger = LoggerFactory.getLogger(QueryCollection.class);
	
	@Override
	public void put(Criteria criteria) {
		validateCriteria(criteria);
		criteriaByIdMap.put(criteria.getCriteriaId(), criteria);
		criteriaByOrbTypeCollection.put(criteria);
	}
	
	@Override
	public Criteria removeByCriteriaId(long id) {
		Criteria criteria = criteriaByIdMap.remove(id);
		criteriaByOrbTypeCollection.removeByCriteriaId(criteria);
		return criteria;
	}
	
	@Override
	public Map<Long, Criteria> removeByOrbTypeId(long id) {
		Map<Long, Criteria> criteriaMap = criteriaByOrbTypeCollection.remove(id);
		criteriaMap = (criteriaMap == null) ? new HashMap<Long, Criteria>() : criteriaMap;
		
		for (long criteriaId: criteriaMap.keySet()) {
			criteriaByIdMap.remove(criteriaId);
		}
		
		return criteriaMap;
	}
	
	@Override
	public void clear() {
		this.criteriaByIdMap.clear();
		this.criteriaByOrbTypeCollection.clear();
	}
}
