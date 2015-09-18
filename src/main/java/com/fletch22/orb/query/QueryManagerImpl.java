package com.fletch22.orb.query;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeConstants;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.query.QueryCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

@Component
public class QueryManagerImpl implements QueryManager {
	
	@Autowired
	Cache cache;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;

	@Loggable4Event
	public long create(Criteria criteria) {
		
		QueryCollection queryCollection = cache.queryCollection;
		
		// Create Query Type Somewhere during startup.
		OrbType orbType = orbTypeManager.getOrbType(OrbTypeConstants.SystemOrbTypes.QUERY.getLabel());
		Orb orb = orbManager.createOrb(orbType.id);
		queryCollection.queries.put(orb.getOrbInternalId(), criteria);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		remove(orb.getOrbInternalId());
		
		return orb.getOrbInternalId();
	}
	
	@Loggable4Event
	public void create(Orb orb, Criteria criteria) {
		
		validateQueryOrb(orb);
		
		QueryCollection queryCollection = cache.queryCollection;
		
		// Create Query Type Somewhere during startup.
		orbManager.createOrb(orb);
		queryCollection.queries.put(orb.getOrbInternalId(), criteria);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		remove(orb.getOrbInternalId());
	}
	
	private void validateQueryOrb(Orb orb) {
		if (OrbTypeConstants.SystemOrbTypes.QUERY.getId() != orb.getOrbTypeInternalId()) {
			throw new RuntimeException("Query orb is not of correct type.");
		}
	}

	@Loggable4Event
	public void remove(long orbInternalIdQuery) {
		QueryCollection queryCollection = cache.queryCollection;
		
		Criteria criteria = queryCollection.queries.remove(orbInternalIdQuery);
		Orb orb = orbManager.deleteOrb(orbInternalIdQuery);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		create(orb, criteria);
	}
}
