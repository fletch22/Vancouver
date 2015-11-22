package com.fletch22.orb.limitation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.AbstractCriteriaManager;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class LimitationManagerImpl extends AbstractCriteriaManager implements LimitationManager {

	@Autowired
	LimitationCollection limitationCollection;

	@Override
	public CriteriaCollection getCriteriaCollection() {
		return limitationCollection;
	}

	@Loggable4Event
	@Override
	public long addDefaultLimitation(Criteria criteria) {
		initializeCriteria(criteria);

		limitationCollection.addDefault(criteria);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		removeDefaultLimitation(criteria.getCriteriaId());
		
		return criteria.getCriteriaId();
	}
	
	@Loggable4Event
	@Override
	public Criteria removeDefaultLimitation(long criteriaId) {
		Criteria criteria = getCriteriaCollection().removeByCriteriaId(criteriaId);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		addDefaultLimitation(criteria);
		
		return criteria;
	}

	@Override
	public List<Criteria> getOrbsDefaultLimitations(long orbTypeInternalId) {
		return limitationCollection.getDefaultLimitations(orbTypeInternalId);
	}
}
