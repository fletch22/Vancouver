package com.fletch22.orb.cache.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
@Scope("prototype")
public class QueryCollection extends CriteriaCollection {
	
	static Logger logger = LoggerFactory.getLogger(QueryCollection.class);
	
	@Override
	public void add(Criteria criteria) {
		validateCriteria(criteria);
		criteriaByIdMap.put(criteria.getCriteriaId(), criteria);
		
		logger.info("Adding criteria to QueryCollection. Not null? {}", criteria != null);
		
		criteriaByOrbTypeCollection.add(criteria);
		
		logger.info("Does criteria exist: {}", this.doesCriteriaExistWithOrbTypeInternalId(criteria.getOrbTypeInternalId()));
	}
	
	@Override
	public Criteria removeByCriteriaId(long id) {
		Criteria criteria = criteriaByIdMap.remove(id);
		criteriaByOrbTypeCollection.removeByCriteriaId(criteria);
		return criteria;
	}
	
	@Override
	public List<Criteria> removeByOrbTypeId(long id) {
		List<Criteria> criteriaList = criteriaByOrbTypeCollection.remove(id);
		criteriaList = (criteriaList == null) ? new ArrayList<Criteria>() : criteriaList;
		for (Criteria criteria : criteriaList) {
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
