package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaImpl;

@Component
@Scope("prototype")
public class QueryCollection extends CriteriaCollection {
	
	static Logger logger = LoggerFactory.getLogger(QueryCollection.class);
	
	@Override
	public void add(CriteriaImpl criteria) {
		validateCriteria(criteria);
		criteriaByIdMap.put(criteria.getCriteriaId(), criteria);
		criteriaByOrbTypeCollection.add(criteria);
	}
	
	@Override
	public CriteriaImpl removeByCriteriaId(long id) {
		CriteriaImpl criteria = criteriaByIdMap.remove(id);
		criteriaByOrbTypeCollection.removeByCriteriaId(criteria);
		return criteria;
	}
	
	@Override
	public List<CriteriaImpl> removeByOrbTypeId(long id) {
		List<CriteriaImpl> criteriaList = criteriaByOrbTypeCollection.remove(id);
		criteriaList = (criteriaList == null) ? new ArrayList<CriteriaImpl>() : criteriaList;
		for (CriteriaImpl criteria : criteriaList) {
			criteriaByIdMap.remove(criteria.getCriteriaId());
		}
		return criteriaList;
	}
	
	@Override
	public void clear() {
		this.criteriaByIdMap.clear();
		this.criteriaByOrbTypeCollection.clear();
	}
}
