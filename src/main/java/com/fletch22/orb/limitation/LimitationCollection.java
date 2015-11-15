package com.fletch22.orb.limitation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.query.CriteriaByOrbTypeCollection;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
@Scope("prototype")
public class LimitationCollection extends CriteriaCollection {
	
	public List<Criteria> criteriaList = new ArrayList<Criteria>();
	
	private CriteriaByOrbTypeCollection defaultCriteria = new CriteriaByOrbTypeCollection();
	
	static Logger logger = LoggerFactory.getLogger(QueryCollection.class);
	
	@Override
	public void add(Criteria criteria) {
		validateCriteria(criteria);
		criteriaByIdMap.put(criteria.getCriteriaId(), criteria);
		criteriaByOrbTypeCollection.add(criteria);
	}
	
	public void addDefault(Criteria criteria) {
		add(criteria);
		defaultCriteria.add(criteria);
	}
	
	public List<Criteria> getDefaultLimitations(long orbTypeInternalId) {
		return defaultCriteria.get(orbTypeInternalId);
	}
	
	@Override
	public Criteria removeByCriteriaId(long id) {
		Criteria criteria = criteriaByIdMap.remove(id);
		criteriaByOrbTypeCollection.removeByCriteriaId(criteria);
		defaultCriteria.removeByCriteriaId(criteria);
		
		return criteria;
	}
	
	@Override
	public List<Criteria> removeByOrbTypeId(long id) {
		this.defaultCriteria.remove(id);
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
		this.defaultCriteria.clear();
	}
}


